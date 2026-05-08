package com.ajh.flow.service;

import com.ajh.flow.domain.Warehouse;
import com.ajh.flow.dto.warehouse.WarehouseRegisterDto;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class WarehouseServiceTest {

    @Autowired WarehouseService warehouseService;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("창고 등록이 정상적으로 되는지 확인")
    public void registerWarehouse() throws Exception{
        //Given
        WarehouseRegisterDto dto = new WarehouseRegisterDto("부산창고","양산시","안진혁","0103804");
        //When
        Long wareHouseId = warehouseService.registerWarehouse(dto);
        em.flush();
        em.clear();
        //Then
        Warehouse warehouse = warehouseService.findById(wareHouseId);
        assertThat(warehouse.getName()).isEqualTo(dto.getName());
        assertThat(warehouse.getAddress()).isEqualTo(dto.getAddress());
        assertThat(warehouse.getManagerName()).isEqualTo(dto.getManagerName());
        assertThat(warehouse.getTel()).isEqualTo(dto.getTel());

    }


}