package com.ajh.flow.service;

import com.ajh.flow.common.constant.ItemUnit;
import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.domain.Item;
import com.ajh.flow.domain.Location;
import com.ajh.flow.domain.Stock;
import com.ajh.flow.domain.Warehouse;
import com.ajh.flow.dto.item.ItemRegisterDto;
import com.ajh.flow.dto.location.LocationRegisterDto;
import com.ajh.flow.dto.stock.StockRegisterDto;
import com.ajh.flow.dto.warehouse.WarehouseRegisterDto;
import com.ajh.flow.repository.StockRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;


@SpringBootTest
@Transactional
class StockServiceTest {
    @Autowired private StockService stockService;
    @Autowired private WarehouseService warehouseService;
    @Autowired private LocationService locationService;
    @Autowired private ItemService itemService;

    @Autowired private StockRepository stockRepository;

    @Autowired
    private EntityManager em;

    private Long warehouseId;
    private Long locationId;
    private Long itemId;

    @BeforeEach
    void setUp() {
        //창고 생성
        warehouseId = warehouseService.registerWarehouse(new WarehouseRegisterDto("부산창고","부산광역시 금정구","안진혁","01038041915"));
        //로케이션 생성
        locationId = locationService.registerLocation(new LocationRegisterDto(warehouseId, LocationZone.COLD,"01","01","01"));
        //상품 생성
        itemId = itemService.registerItem(new ItemRegisterDto("사과",1000L,ItemUnit.EA,"두쫀쿠사과"));
    }

    @Test
    @DisplayName("최초 입고시 Stock Entity 생성")
    public void addNewStock() throws Exception{
        //Given - 상품 등록
        Long stockId = stockService.registerStock(new StockRegisterDto(itemId, locationId, 1000L));
        //When
        em.flush();
        em.clear();
        //Then
        assertThat(stockService.findById(stockId).getQuantity()).isNotEqualTo(100L);
        assertThat(stockService.findById(stockId).getQuantity()).isEqualTo(1000L);

    }
}