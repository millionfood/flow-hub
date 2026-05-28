package com.ajh.flow.service;

import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.common.constant.UserRole;
import com.ajh.flow.domain.Warehouse;
import com.ajh.flow.dto.user.UserRegisterDto;
import com.ajh.flow.dto.warehouse.WarehouseRegisterDto;
import com.ajh.flow.dto.warehouse.WarehouseUpdateDto;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
    @Autowired UserService userService;

    @Autowired
    EntityManager em;

    WarehouseRegisterDto dto1;
    Long wareHouseId1;

    @BeforeEach
    void setUp() {
        Long userId = userService.registerUser(new UserRegisterDto("millionfood@naver.com","12312312312","안진혁", UserRole.USER,"01038041915"));
        dto1 = new WarehouseRegisterDto("부산창고","양산시",userId);
        wareHouseId1 = warehouseService.registerWarehouse(dto1);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("창고 등록이 정상적으로 되는지 확인")
    public void registerWarehouse() throws Exception{
        //Given
        //When
        //Then
        Warehouse warehouse = warehouseService.findById(wareHouseId1);
        assertThat(warehouse.getName()).isEqualTo(dto1.getName());
        assertThat(warehouse.getAddress()).isEqualTo(dto1.getAddress());
        assertThat(warehouse.getRegister().getId()).isEqualTo(dto1.getRegisterId());

    }

    @Test
    @DisplayName("창고 정보 수정이 정상적으로 되는지 확인")
    public void updateWarehouse() throws Exception{
        //Given - 창고 등록 후 영속성 컨텍스트 비우기
        //When - 업데이트
        warehouseService.updateWarehouse(wareHouseId1,new WarehouseUpdateDto("서울창고","서울시"));
        em.flush();
        em.clear();

        //Then
        Warehouse warehouse = warehouseService.findById(wareHouseId1);
        assertThat(warehouse.getName()).isEqualTo("서울창고");
        assertThat(warehouse.getAddress()).isEqualTo("서울시");
        assertThat(warehouse.getRegister()).isEqualTo("안진웅");


    }

    @Test
    @DisplayName("창고 사용 상태 변경이 정상적으로 되는지 확인")
    public void changeStatusWarehouse() throws Exception{
        //Given - 창고 등록 후 영속성 컨텍스트 비우기
        //When - 상태 변경(미사용)
        warehouseService.stopUseWarehouse(wareHouseId1);
        em.flush();
        em.clear();
        //Then
        Warehouse warehouse1 = warehouseService.findById(wareHouseId1);
        assertThat(warehouse1.getUseYn()).isEqualTo(UseYn.N);
        em.clear();

        //When - 상태 변경(재사용)
        warehouseService.reUseWarehouse(wareHouseId1);
        em.flush();
        em.clear();
        //Then
        Warehouse warehouse2 = warehouseService.findById(wareHouseId1);
        assertThat(warehouse2.getUseYn()).isEqualTo(UseYn.Y);

    }


}