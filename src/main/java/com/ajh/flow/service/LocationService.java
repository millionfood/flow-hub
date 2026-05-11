package com.ajh.flow.service;

import com.ajh.flow.common.exception.EntityNotFoundException;
import com.ajh.flow.common.exception.InvalidLocationException;
import com.ajh.flow.domain.Item;
import com.ajh.flow.domain.Location;
import com.ajh.flow.domain.Warehouse;
import com.ajh.flow.dto.location.LocationDetailDto;
import com.ajh.flow.dto.location.LocationRegisterDto;
import com.ajh.flow.dto.location.LocationUpdateDto;
import com.ajh.flow.repository.ItemRepository;
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
    private final ItemRepository itemRepository;

    private final WarehouseService warehouseService;

    //-----------------등록-------------------
    @Transactional
    public Long registerLocation(LocationRegisterDto dto){
        //locCode는 entity로 변환되면서 builder에 의해서 생성됨
        Location location = dto.toVo();
        //db에 중복도니 locCode가 있는지 확인
        if(locationRepository.existsByLocCode(dto.getWarehouseId(),dto.getZone(),location.getLocCode())) {
            throw new InvalidLocationException("해당 창고의 locCode는 이미 존재합니다.");
        }
        //warehouse 들고오기 - location 엔티티에 넣어줘야함(dto에는 없음)
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
//    public List<LocationDetailDto> findInboundableLocations(){
//
//    }
    public Location findById(Long id){
        return locationRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }
    public List<LocationDetailDto> findMoveableLocations(Long itemId, Long locationId){
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        Location location = locationRepository.findById(locationId)
                .orElseThrow(EntityNotFoundException::new);
        return locationRepository.findMoveableLocations(item,location)
                .stream().map(LocationDetailDto::new)
                .collect(Collectors.toList());
    }

    //-----------------수정-------------------
    @Transactional
    public void updateLocation(Long id, LocationUpdateDto dto){
        //dto에는 warehouse가 없기에 따로 불러와서 넣어주어야 한다.
        Warehouse warehouse = warehouseService.findById(dto.getWarehouseId());
        //해당 엔티티 수정하고 트랜잭션 마무리
        Location location = locationRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        location.update(dto,warehouse);
    }

    //-----------------상태변경-------------------
    @Transactional
    public void stopUseLocation(Long id){
        Location location = findById(id);
        location.stopUse();
    }
    @Transactional
    public void reUseLocation(Long id){
        Location location = findById(id);
        location.reUse();
    }


    //-----------------기타-------------------
}
