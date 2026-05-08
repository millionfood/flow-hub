package com.ajh.flow.service;

import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.domain.Warehouse;
import com.ajh.flow.dto.warehouse.WarehouseRegisterDto;
import com.ajh.flow.dto.warehouse.WarehouseUpdateDto;
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

    @Test
    @DisplayName("창고 정보 수정이 정상적으로 되는지 확인")
    public void updateWarehouse() throws Exception{
        //Given - 창고 등록 후 영속성 컨텍스트 비우기
        WarehouseRegisterDto dto = new WarehouseRegisterDto("부산창고","양산시","안진혁","0103804");

        Long wareHouseId = warehouseService.registerWarehouse(dto);
        em.flush();
        em.clear();

        //When - 업데이트
        warehouseService.updateWarehouse(wareHouseId,new WarehouseUpdateDto("서울창고","서울시","안진웅","0000"));
        em.flush();
        em.clear();

        //Then
        Warehouse warehouse = warehouseService.findById(wareHouseId);
        assertThat(warehouse.getName()).isEqualTo("서울창고");
        assertThat(warehouse.getAddress()).isEqualTo("서울시");
        assertThat(warehouse.getManagerName()).isEqualTo("안진웅");
        assertThat(warehouse.getTel()).isEqualTo("0000");


    }

    @Test
    @DisplayName("창고 사용 상태 변경이 정상적으로 되는지 확인")
    public void changeStatusWarehouse() throws Exception{
        //Given - 창고 등록 후 영속성 컨텍스트 비우기
        WarehouseRegisterDto dto = new WarehouseRegisterDto("부산창고","양산시","안진혁","0103804");

        Long wareHouseId = warehouseService.registerWarehouse(dto);
        em.flush();
        em.clear();

        //When - 상태 변경(미사용)
        warehouseService.stopUseWarehouse(wareHouseId);
        em.flush();
        em.clear();
        //Then
        Warehouse warehouse1 = warehouseService.findById(wareHouseId);
        assertThat(warehouse1.getUseYn()).isEqualTo(UseYn.N);
        em.clear();

        //When - 상태 변경(재사용)
        warehouseService.reUseWarehouse(wareHouseId);
        em.flush();
        em.clear();
        //Then
        Warehouse warehouse2 = warehouseService.findById(wareHouseId);
        assertThat(warehouse2.getUseYn()).isEqualTo(UseYn.Y);

    }


}