package com.ajh.flow.service;

import com.ajh.flow.common.constant.StockTransactionType;
import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.common.exception.EntityNotFoundException;
import com.ajh.flow.common.exception.InsufficientStockException;
import com.ajh.flow.common.exception.InvalidStockException;
import com.ajh.flow.domain.Item;
import com.ajh.flow.domain.Location;
import com.ajh.flow.domain.Stock;
import com.ajh.flow.dto.stock.StockDetailDto;
import com.ajh.flow.dto.stock.StockMoveDto;
import com.ajh.flow.dto.stock.StockRegisterDto;
import com.ajh.flow.dto.stock.StockUpdateDto;
import com.ajh.flow.repository.ItemRepository;
import com.ajh.flow.repository.LocationRepository;
import com.ajh.flow.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockRepository stockRepository;
    private final LocationRepository locationRepository;
    private final ItemRepository itemRepository;



    //-----------------등록-------------------
    //재고 입고
    @Transactional
    public Long registerStock(StockRegisterDto dto){
        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(EntityNotFoundException::new);
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        //로케이션과 상품이 현재 사용 가능한 상태인지 확인
        existLocationAndItem(location, item);

        //로케이션에 동일한 상태의 아이템이 있는지 확인
        return stockRepository.find_Same_Location_Item_Status(location.getId(),item.getId(),dto.getStatus())
                .map(stock ->{
                    stock.addQuantity(dto.getQuantity());
                    return stock.getId();
                })
                .orElseGet(()->{
                    Stock stock = Stock.builder()
                            .item(item)
                            .location(location)
                            .quantity(dto.getQuantity())
                            .status(dto.getStatus())
                            .build();

                    stockRepository.save(stock);
                    //재고 이력 남기기
                    return stock.getId();
                });

    }


    //-----------------조회-------------------
    //전체 조회
    public List<StockDetailDto> findAll(){
        return stockRepository.findAll();
    }
    //단건 조회 - 엔티티
    public Stock findById(Long id){
        return stockRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }
    //단건 조회 - dto
    public StockDetailDto findDetailDtoById(Long id){
        return stockRepository.findDetailDtoById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    //-----------------상태변경-------------------
    //단순 수정
    @Transactional
    public void updateStock(Long id, StockUpdateDto dto){
        Stock stock = stockRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        stock.update(dto);
    }

    //재고 이동
    @Transactional
    public Long moveStock(Long id, StockMoveDto dto){
        Long returnValue;
        Stock oldStock = findById(id);
        Location toLocation = locationRepository.findById(dto.getToLocationId())
                .orElseThrow(EntityNotFoundException::new);
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        //1.이동 수량이 기존의 재고보다 적은가
        if(oldStock.getQuantity() < dto.getMoveQuantity()){
            throw new InsufficientStockException("이동하려는 수량이 기존의 수량을 초과합니다.");
        }
        //2.목적지의 로케이션이 이용 가능한가/해당 상품이 이용 가능한가(useYn)
        existLocationAndItem(toLocation, item);
        //2-1.해당 목적지가 존재하는지는 검증할 필요가 없다(선택지에 없기때문)

        //3.목적지의 로케이션에 상품이 있는가? - 해당 zone+로케이션 번호를 가진 상품 조회
        List<Stock> newStockList = stockRepository.findByLocation(dto.getToLocationId());
        //3-1.없다면 신규 입고 로직
        if(newStockList == null ||newStockList.isEmpty()){
            //새로운 곳의 재고 등록
            returnValue = registerStock(new StockRegisterDto(dto));

            //기존 재고의 감소/삭제
            minusOrRemove(oldStock,dto);
        }else{
            //3-2.있다면 동일한 상품인가? - 하나만 확인하면 됨, 어차피 상품은 동일
            if(oldStock.getItem().getId().equals(newStockList.getFirst().getItem().getId())){
                //3-3.동일한 상품이라면 동일한 상태가 있는지 확인
                Stock sameStatusStock = newStockList.stream()
                        .filter(s -> Objects.equals(s.getStatus(),oldStock.getStatus()))
                                .findFirst()
                                        .orElse(null);
                if(sameStatusStock != null){
                    sameStatusStock.addQuantity(dto.getMoveQuantity());
                    returnValue =  sameStatusStock.getId();
                }else{
                    returnValue = registerStock(new StockRegisterDto(dto));
                }
                //기존 재고 감소/삭제
                minusOrRemove(oldStock,dto);

            }else{
                //3-4.동일한 상품이 아니라면 재고 추가 x (한 로케이션에 하나의 상품만)
                //이 로직에 도달할 일은 없어야 하지만 혹시 몰라 방어한다.
                throw new InvalidStockException("해당 로케이션에 다른 상품이 존재합니다.");
            }
        }
        return  returnValue;
        //4.히스토리 기록
    }
    //-----------------삭제-------------------
    @Transactional
    public void deleteStock(Long id){
        stockRepository.delete(id);
        //히스토리 기록
    }

    //-----------------기타-------------------
    //로케이션과 아이템을 이용가능한지 확인
    public void existLocationAndItem(Location location, Item item){
        if(location.getUseYn() == UseYn.N){
            log.error("이용할 수 없는 로케이션 입니다.");
            throw new InvalidStockException("해당 로케이션은 이용할 수 없습니다.");
        }
        if(item.getUseYn() == UseYn.N){
            log.error("이용할 수 없는 상품 입니다.");
            throw new InvalidStockException("해당 상품은 이용할 수 없습니다.");
        }
    }
    //기존 재고 수량 감소/삭제
    public void minusOrRemove(Stock oldStock, StockMoveDto dto){
        //기존 재고의 감소/삭제
        if(Objects.equals(oldStock.getQuantity(), dto.getMoveQuantity())){
            //만약 기존의 재고의 수량과 이동량이 동일하다면
            stockRepository.delete(oldStock.getId());
        }else{
            //아니라면 재고의 수량 감소
            oldStock.removeQuantity(dto.getMoveQuantity());
        }
    }

}
