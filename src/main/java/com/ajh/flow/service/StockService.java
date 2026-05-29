package com.ajh.flow.service;

import com.ajh.flow.common.constant.StockTransactionType;
import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.common.exception.EntityNotFoundException;
import com.ajh.flow.common.exception.InsufficientStockException;
import com.ajh.flow.common.exception.InvalidStockException;
import com.ajh.flow.domain.*;
import com.ajh.flow.dto.item.ItemLocationDetailDto;
import com.ajh.flow.dto.item.ItemLocationSearchCond;
import com.ajh.flow.dto.stock.*;
import com.ajh.flow.repository.HistoryRepository;
import com.ajh.flow.repository.ItemRepository;
import com.ajh.flow.repository.LocationRepository;
import com.ajh.flow.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final HistoryRepository historyRepository;



    //-----------------등록-------------------
    //재고 입고
    @Transactional
    public Long registerStockWithSecurity(StockRegisterDto dto, User user){
        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(EntityNotFoundException::new);
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        //로케이션과 상품이 현재 사용 가능한 상태인지 확인
        existLocationAndItem(location, item);

        //로케이션에 동일한 상태의 아이템이 있는지 확인
        return stockRepository.find_Same_Location_Item_Status(location.getId(),item.getId(),dto.getStatus())
                .map(stock ->{
                    //동일한 상태의 아이템이 있다면
                    Long preQuantity = stock.getQuantity();
                    Long moveQuantity = dto.getQuantity();
                    //stock 재고 추가
                    stock.addQuantity(dto.getQuantity());
                    //히스토리 기록(stock의 기존 재고 전달)
                    StockHistory history = StockHistory.createStockHistory(stock,user,preQuantity,moveQuantity,StockTransactionType.IN,"기존재고 추가.");
                    historyRepository.saveStockHistory(history);




                    return stock.getId();
                })
                .orElseGet(()->{
                    //동일한 상태의 아이템이 없다면
                    Long preQuantity = 0L;
                    Long moveQuantity = dto.getQuantity();
                    Stock stock = Stock.builder()
                            .item(item)
                            .location(location)
                            .quantity(dto.getQuantity())
                            .status(dto.getStatus())
                            .build();
                    stockRepository.save(stock);

                    //히스토리 기록
                    StockHistory history = StockHistory.createStockHistory(stock,user,preQuantity,moveQuantity,StockTransactionType.IN,"새로운 재고 입고.");
                    historyRepository.saveStockHistory(history);

                    return stock.getId();
                });

    }
    //테스트용
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
                    return stock.getId();
                });

    }


    //-----------------조회-------------------
    //전체 조회
    public List<StockDetailDto> findAll(){
        return stockRepository.findAll();
    }
    public Page<ItemLocationDetailDto> findItemWidthPaging(Long itemId, Pageable pageable, ItemLocationSearchCond cond){
        return stockRepository.findAllWithPaging(itemId,pageable,cond);
    }
    public Page<StockDetailDto> findAllDetailWithPaging(Pageable pageable, StockSearchCond cond){
        return stockRepository.findAllDetailWithPaging(pageable,cond);
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
    public void updateStock(Long id, StockUpdateDto dto, User user){
        Stock stock = stockRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        Long preQuantity = stock.getQuantity(); //1000
        Long moveQuantity = dto.getQuantity() - preQuantity; //-900
        stock.update(dto);

        if(!preQuantity.equals(dto.getQuantity())){
            //히스토리 기록
            if(preQuantity.compareTo(dto.getQuantity()) < 0){
                StockHistory history = StockHistory.createStockHistory(stock,user,preQuantity,moveQuantity,StockTransactionType.ADJ,"기존 재고 수정.");
                historyRepository.saveStockHistory(history);
            }else{
                StockHistory history = StockHistory.createStockHistory(stock,user,preQuantity,moveQuantity,StockTransactionType.ADJ,"기존 재고 수정.");
                historyRepository.saveStockHistory(history);
            }
        }

    }

    //재고 이동
    @Transactional
    public Long moveStock(Long id, StockMoveDto dto, User user){
        Long returnValue;
        Stock oldStock = findById(id);
        //이동할 곳의 location과 이동하는 item 엔티티를 가져온다.
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
            Stock stock = Stock.builder()
                    .item(item)
                    .location(toLocation)
                    .quantity(dto.getMoveQuantity())
                    .status(dto.getStatus())
                    .build();
            stockRepository.save(stock);
            returnValue = stock.getId();
            //히스토리 기록 - 기존 재고의 증가
            StockHistory newHistory = StockHistory.createStockHistory(stock,user,0L,dto.getMoveQuantity(),StockTransactionType.MOVE,"기존 재고 감소.");
            historyRepository.saveStockHistory(newHistory);

            //기존 재고의 감소/삭제
            Long oldStockPreQuantity = oldStock.getQuantity();
            minusOrRemove(oldStock,dto);
            //히스토리 기록 - 기존 재고의 감소
            StockHistory oldHistory = StockHistory.createStockHistory(oldStock,user,oldStockPreQuantity,-dto.getMoveQuantity(),StockTransactionType.MOVE,"기존 재고 감소.");
            historyRepository.saveStockHistory(oldHistory);
        }else{
            //3-2.있다면 동일한 상품인가? - 하나만 확인하면 됨, 어차피 상품은 동일
            if(oldStock.getItem().getId().equals(newStockList.getFirst().getItem().getId())){
                //3-3.동일한 상품이라면 동일한 상태가 있는지 확인
                Stock sameStatusStock = newStockList.stream()
                        .filter(s -> Objects.equals(s.getStatus(),oldStock.getStatus()))
                                .findFirst()
                                        .orElse(null);
                if(sameStatusStock != null){
                    //같은 아이템이고 같은 상태의 재고가 있다면 변경 감지를 통한 재고 추가
                    Long preQuantity = sameStatusStock.getQuantity();

                    sameStatusStock.addQuantity(dto.getMoveQuantity());
                    returnValue =  sameStatusStock.getId();

                    //히스토리 기록 - 이동하는 곳의 재고 증가
                    StockHistory history = StockHistory.createStockHistory(sameStatusStock,user,preQuantity,dto.getMoveQuantity(),StockTransactionType.MOVE,"이동하는 곳의 재고 증가.");
                    historyRepository.saveStockHistory(history);
                }else{
                    //같은 아이템이지만 다른 상태의 재고라면 새로운 재고 추가(
                    Stock stock = Stock.builder()
                            .item(item)
                            .location(toLocation)
                            .quantity(dto.getMoveQuantity())
                            .status(dto.getStatus())
                            .build();
                    stockRepository.save(stock);
                    returnValue =  stock.getId();
                    //히스토리 기록 - 새로운 재고 추가
                    StockHistory newHistory = StockHistory.createStockHistory(stock,user,0L,dto.getMoveQuantity(),StockTransactionType.MOVE,"기존 재고 감소.");
                    historyRepository.saveStockHistory(newHistory);
                }
                //기존 재고 감소/삭제
                Long preQuantity = oldStock.getQuantity();

                minusOrRemove(oldStock,dto);

                //히스토리 기록 - 기존 재고의 감소
                StockHistory oldHistory = StockHistory.createStockHistory(oldStock,user,preQuantity,-dto.getMoveQuantity(),StockTransactionType.MOVE,"기존 재고 감소.");
                historyRepository.saveStockHistory(oldHistory);

            }else{
                //3-4.동일한 상품이 아니라면 재고 추가 x (한 로케이션에 하나의 상품만)
                //이 로직에 도달할 일은 없어야 하지만 혹시 몰라 방어한다.
                throw new InvalidStockException("해당 로케이션에 다른 상품이 존재합니다.");
            }
        }
        return  returnValue;
    }
    //-----------------삭제-------------------
    @Transactional
    public void deleteStock(Long id,User user){
        Stock stock = findById(id);
        Long deleteQuantity = stock.getQuantity();

        //히스토리 기록 - 기존 재고의 삭제
        StockHistory history = StockHistory.createStockHistory(stock,user,deleteQuantity,-deleteQuantity,StockTransactionType.OUT,"기존 재고 폐기.");
        historyRepository.saveStockHistory(history);

        stockRepository.delete(id);
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
