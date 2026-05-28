package com.ajh.flow.service;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.common.constant.UserRole;
import com.ajh.flow.common.exception.InvalidLocationException;
import com.ajh.flow.domain.Location;
import com.ajh.flow.dto.location.LocationRegisterDto;
import com.ajh.flow.dto.user.UserRegisterDto;
import com.ajh.flow.dto.warehouse.WarehouseRegisterDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class LocationServiceTest {

    @Autowired WarehouseService warehouseService;
    @Autowired LocationService locationService;
    @Autowired UserService userService;

    @Autowired EntityManager em;

    private Long warehouseId1;
    private Long warehouseId2;
    private Long locationId1;
    private Long locationId2;
    List<String> levels = new ArrayList<>();

    @BeforeEach
    void setUp() {
        levels.add("01");
        Long userId = userService.registerUser(new UserRegisterDto("millionfood@naver.com","12312312312","안진혁", UserRole.USER,"01038041915"));
        warehouseId1 = warehouseService.registerWarehouse(new WarehouseRegisterDto("부산창고","부산광역시 금정구",userId));
        warehouseId2 = warehouseService.registerWarehouse(new WarehouseRegisterDto("서울창고","서울특별시 송파구",userId));
        locationId1 = locationService.registerLocation(new LocationRegisterDto(warehouseId1, LocationZone.COLD,"01","01",levels));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("location이 정상적으로 등록되어야 한다.")
    public void registerLocation() throws Exception{
        //Given warehouse 등록 후, location등록
        //When - 다시 location 객체를 꺼내온다.
        Location location =  locationService.findById(locationId1);
        //Then
        assertThat(location.getLocCode()).isEqualTo("C-01-01-01");

    }

    @Test
    @DisplayName("warehouse+zone+locCode가 중복되면 안된다.")
    public void duplicateLocCode() throws Exception{
        //Given warehouse 등록 후, location등록
        //When 동일한 warehouse+zone+locCode
        LocationRegisterDto dto = new LocationRegisterDto(1L, LocationZone.COLD,"01","01",levels);
        //Then
        assertThrows(InvalidLocationException.class,()->locationService.registerLocation(dto));
    }

    @Test
    @DisplayName("다른 창고의 zone+locCode 는 통과되어야 한다.")
    public void diffWarehouseAndDuplicateLocCode() throws Exception{
        //Given warehouse 등록 후, location등록
        //When
        // 같은창고의 다른 zone + 같은 locCode
        LocationRegisterDto dto1 = new LocationRegisterDto(warehouseId1, LocationZone.FRIDGE,"01","01",levels);
        // 다른창고의 동일한 zone+locCode
        LocationRegisterDto dto2 = new LocationRegisterDto(warehouseId2, LocationZone.COLD,"01","01",levels);
        //Then
        assertDoesNotThrow(()->locationService.registerLocation(dto1));
        assertDoesNotThrow(()->locationService.registerLocation(dto2));
    }

    @Test
    @DisplayName("로케이션 사용을 중지합니다.")
    public void stopUse() throws Exception{
        //Given
        //When
        locationService.stopUseLocation(locationId1);
        em.flush();
        em.clear();
        //Then
        assertThat(locationService.findById(locationId1).getUseYn()).isEqualTo(UseYn.N);
    }

    @Test
    @DisplayName("로케이션을 재사용 합니다")
    public void reUse() throws Exception{
        //Given
        //When
        locationService.reUseLocation(locationId1);
        em.flush();
        em.clear();
        //Then
        assertThat(locationService.findById(locationId1).getUseYn()).isEqualTo(UseYn.Y);
    }

}