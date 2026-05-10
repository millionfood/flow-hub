package com.ajh.flow.service;

import com.ajh.flow.common.constant.StockTransactionType;
import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.common.exception.EntityNotFoundException;
import com.ajh.flow.common.exception.InsufficientStockException;
import com.ajh.flow.common.exception.InvalidStockException;
import com.ajh.flow.domain.History;
import com.ajh.flow.domain.Item;
import com.ajh.flow.domain.Location;
import com.ajh.flow.domain.Stock;
import com.ajh.flow.dto.stock.StockDetailDto;
import com.ajh.flow.dto.stock.StockRegisterDto;
import com.ajh.flow.repository.HistoryRepository;
import com.ajh.flow.repository.ItemRepository;
import com.ajh.flow.repository.LocationRepository;
import com.ajh.flow.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    private final ItemService itemService;
    private final LocationService locationService;


    //-----------------등록-------------------
    //재고 입고
    @Transactional
    public Long registerStock(StockRegisterDto dto){

        //로케이션과 상품이 현재 사용 가능한 상태인지 확인
        Location location = locationService.findById(dto.getLocationId());
        Item item = itemService.findById(dto.getItemId());
        if(location.getUseYn() == UseYn.N){
            throw new InvalidStockException("이용할 수 없는 창고 입니다.");
        }
        if(item.getUseYn() == UseYn.N){
            throw new InvalidStockException("이용할 수 없는 상품 입니다.");
        }
        //로케이션에 동일한 아이템이 있는지 확인
        if(stockRepository.existsByLocationAndItem(location.getId(),item.getId())){
            throw new InvalidStockException("이미 동일한 위치에 동일한 상품이 존재합니다. - 재고 수정을 이용해 주세요.");
        }

        //상품 생성
        Stock stock = Stock.builder()
                .item(item)
                .location(location)
                .quantity(dto.getQuantity())
                .status(dto.getStatus())
                .build();

        stockRepository.save(stock);
        //재고 이력 남기기
        return stock.getId();
    }


    //-----------------조회-------------------
    //전체 조회
    public List<StockDetailDto> findAll(){
        return stockRepository.findAll();
    }
    //단건 조회
    public Stock findById(Long id){
        return stockRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    //-----------------상태변경-------------------


}
