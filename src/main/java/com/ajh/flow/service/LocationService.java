package com.ajh.flow.service;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.common.exception.EntityNotFoundException;
import com.ajh.flow.common.exception.InvalidLocationException;
import com.ajh.flow.domain.Item;
import com.ajh.flow.domain.Location;
import com.ajh.flow.domain.Warehouse;
import com.ajh.flow.dto.location.LocationDetailDto;
import com.ajh.flow.dto.location.LocationRegisterDto;
import com.ajh.flow.dto.location.LocationSearchCond;
import com.ajh.flow.dto.location.LocationUpdateDto;
import com.ajh.flow.repository.ItemRepository;
import com.ajh.flow.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        //warehouse 들고오기 - location 엔티티에 넣어줘야함(dto에는 없음)
        Warehouse warehouse = warehouseService.findById(dto.getWarehouseId());

        List<String> levels = dto.getSelectedLevels();
        LocationZone zone = dto.getZone();
        long successCnt = 0;

        for(String lvl : levels){

            Location location = dto.toVo(warehouse,lvl);
            //db에 중복되는 locCode가 있는지 확인 - 다른 사용자랑 겹칠 경우를 대비
            if(locationRepository.existsByLocCode(dto.getWarehouseId(),zone,location.getLocCode())) {
                throw new InvalidLocationException("해당 창고의 locCode는 이미 존재합니다. - 다른 사용자가 직전에 등록하였습니다.");
            }

            locationRepository.save(location);
            successCnt++;
        }
        return successCnt;

    }
    @Transactional
    public Long registerLocationOne(LocationRegisterDto dto){
        //warehouse 들고오기 - location 엔티티에 넣어줘야함(dto에는 없음)
        Warehouse warehouse = warehouseService.findById(dto.getWarehouseId());

        Location location = dto.toVo(warehouse,dto.getSelectedLevels().getFirst());
        //db에 중복되는 locCode가 있는지 확인 - 다른 사용자랑 겹칠 경우를 대비
        if(locationRepository.existsByLocCode(dto.getWarehouseId(),dto.getZone(),location.getLocCode())) {
            throw new InvalidLocationException("해당 창고의 locCode는 이미 존재합니다. - 다른 사용자가 직전에 등록하였습니다.");
        }

        locationRepository.save(location);

        return location.getId();

    }


    //-----------------조회-------------------
    public List<LocationDetailDto> findAll(){
        return locationRepository.findAll().stream()
                .map(LocationDetailDto::new).collect(Collectors.toList());
    }
    public Page<LocationDetailDto> findAllWidthPaging(Pageable pageable, LocationSearchCond cond){
        return locationRepository.findAllWithPaging(pageable,cond).map(LocationDetailDto::new);
    }
    public List<LocationDetailDto> findInboundAbleALlLocationByItem(Long itemId,Long warehouseId){
        return locationRepository.findInboundAbleALlLocation(itemId,warehouseId)
                .stream()
                .map(LocationDetailDto::new)
                .collect(Collectors.toList());
    }
    public List<LocationDetailDto> findInboundAbleALlLocationByWareHouse(Long warehouseId){
        return locationRepository.findInboundAbleLocationByWarehouse(warehouseId)
                .stream().map(LocationDetailDto::new).collect(Collectors.toList());
    }
    public Location findById(Long id){
        return locationRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }
    public List<LocationDetailDto> findMoveableLocations(Long warehouseId,Long itemId, Long locationId, StockStatus status){
        return locationRepository.findMoveableLocations(warehouseId,itemId,locationId,status)
                .stream().map(LocationDetailDto::new)
                .collect(Collectors.toList());
    }
    public List<String> findExistingLevels(Long warehouseId, String zoneStr, String row, String col){
        LocationZone zone = LocationZone.valueOf(zoneStr);

        return locationRepository.findLevelsByZone_Row_Col(warehouseId, zone, row, col);
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
    //엑셀용 데이터 추출
    public List<LocationDetailDto> getExcelDownloadList(LocationSearchCond cond){
        return locationRepository.findExcelList(cond)
                .stream().map(LocationDetailDto::new).collect(Collectors.toList());
    }
}
