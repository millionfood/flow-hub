package com.ajh.flow.service;

import com.ajh.flow.common.exception.EntityNotFoundException;
import com.ajh.flow.common.exception.InvalidLocationException;
import com.ajh.flow.domain.Location;
import com.ajh.flow.domain.Warehouse;
import com.ajh.flow.dto.location.LocationDetailDto;
import com.ajh.flow.dto.location.LocationRegisterDto;
import com.ajh.flow.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final WarehouseService warehouseService;

    //-----------------등록-------------------
    @Transactional
    public Long registerLocation(LocationRegisterDto dto){
        Location location = dto.toVo();
        //db에 중복도니 locCode가 있는지 확인
        if(locationRepository.existsByLocCode(location.getLocCode())) {
            throw new InvalidLocationException("해당 locCode는 이미 존재합니다.");
        }
        //dto에 들어 있는 warehouseId값으로 db에서 해당 객체 들고오기
        Warehouse warehouse = warehouseService.findById(dto.getWarehouseId());
        location.insertWarehouse(warehouse);

        locationRepository.save(location);
        return location.getId();
    }


    //-----------------조회-------------------
    public List<LocationDetailDto> findAll(){
        return locationRepository.findAll().stream()
                .map(LocationDetailDto::new)
                .collect(Collectors.toList());
    }
    public Location findById(Long id){
        return locationRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    //-----------------수정-------------------


    //-----------------상태변경-------------------

}
