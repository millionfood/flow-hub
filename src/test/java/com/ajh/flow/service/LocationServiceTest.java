package com.ajh.flow.service;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.exception.InvalidLocationException;
import com.ajh.flow.domain.Location;
import com.ajh.flow.dto.location.LocationRegisterDto;
import com.ajh.flow.dto.warehouse.WarehouseRegisterDto;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LocationServiceTest {

    @Autowired WarehouseService warehouseService;
    @Autowired LocationService locationService;
    @Autowired EntityManager em;

    @Test
    @DisplayName("location이 정상적으로 등록되어야 한다.")
    public void registerLocation() throws Exception{
        //Given warehouse 등록 후, location등록
        warehouseService.registerWarehouse(new WarehouseRegisterDto("부산창고","부산광역시 금정구","안진혁","01038041915"));
        Long locationId = locationService.registerLocation(new LocationRegisterDto(1L, LocationZone.COLD,"01","01","01"));
        em.flush();
        em.clear();
        //When - 다시 location 객체를 꺼내온다.
        Location location =  locationService.findById(locationId);
        //Then
        assertThat(location.getLocCode()).isEqualTo("C-01-01-01");

    }

    @Test
    @DisplayName("locCode가 중복되면 에러가 발생해야 한다.")
    public void duplicateLocCode() throws Exception{
        //Given warehouse 등록 후, location등록
        warehouseService.registerWarehouse(new WarehouseRegisterDto("부산창고","부산광역시 금정구","안진혁","01038041915"));
        locationService.registerLocation(new LocationRegisterDto(1L, LocationZone.COLD,"01","01","01"));
        em.flush();
        em.clear();
        //When - 동일한 locCode의 dto 생성
        LocationRegisterDto dto = new LocationRegisterDto(1L, LocationZone.COLD,"01","01","01");
        //Then
        assertThrows(InvalidLocationException.class,()->locationService.registerLocation(dto));
    }

}