package com.ajh.flow.service;

import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.common.exception.EntityNotFoundException;
import com.ajh.flow.common.exception.InvalidAddressException;
import com.ajh.flow.domain.Location;
import com.ajh.flow.domain.User;
import com.ajh.flow.domain.Warehouse;
import com.ajh.flow.dto.warehouse.WarehouseDetailDto;
import com.ajh.flow.dto.warehouse.WarehouseRegisterDto;
import com.ajh.flow.dto.warehouse.WarehouseSearchCond;
import com.ajh.flow.dto.warehouse.WarehouseUpdateDto;
import com.ajh.flow.repository.LocationRepository;
import com.ajh.flow.repository.UserRepository;
import com.ajh.flow.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;


    //-----------------등록-------------------
    @Transactional
    public Long registerWarehouse(WarehouseRegisterDto dto) {
        Warehouse warehouse = dto.toVo();

        String address = warehouse.getAddress();

        //해당하는 주소가 이미 등록되어 있는지 체크
        if(warehouseRepository.existsByAddress(address)) {
            throw new InvalidAddressException("이미 존재하는 주소입니다.");
        }
        //registerDto에는 사용자 객체가 없고, 사용자 id만 있다 - 엔티티를 불러와서 따로 연결
        User user = userRepository.findById(dto.getRegisterId())
                        .orElseThrow(EntityNotFoundException::new);
        warehouse.setRegister(user);

        warehouseRepository.save(warehouse);
        return warehouse.getId();
    }

    //-----------------조회-------------------
    public List<WarehouseDetailDto> findAll() {
        //주의 여기에는 tel 필드가 들어 있지 않음
        return warehouseRepository.findAll()
                .stream()
                .map(WarehouseDetailDto::new)
                .collect(Collectors.toList());
    }
    public List<WarehouseDetailDto> findInboundAbleWarehouses() {
        //주의 여기에는 tel 필드가 들어 있지 않음
        return warehouseRepository.findInboundAbleWarehouses()
                .stream()
                .map(WarehouseDetailDto::new)
                .collect(Collectors.toList());
    }
    public Page<WarehouseDetailDto> findAllWithPaging(Pageable pageable, WarehouseSearchCond cond) {
        return warehouseRepository.findAllDetailWithPaging(pageable,cond);
    }

    public Warehouse findById(Long id){
        return warehouseRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

//    public WarehouseDetailDto findDetailById(Long id){
//
//    }

    //-----------------수정-------------------
    @Transactional
    public void updateWarehouse(Long id, WarehouseUpdateDto dto){
        //창고 조회 (영속성 컨텍스트에 넣기)
        Warehouse warehouse = findById(id);
        //해당 엔티티 수정하고 트랜잭션 마무리
        warehouse.update(dto);
    }

    //-----------------useYn 상태 변경-------------------
    //사용하지 않음
    @Transactional
    public void stopUseWarehouse(Long id){
        //창고 조회 (영속성 컨텍스트에 넣기)
        Warehouse warehouse = findById(id);
        //warehouse와 location 엔티티 수정
        warehouse.setUseYn(UseYn.N);
        List<Location> locationList = locationRepository.findLocationsByWarehouse(id);
        for(Location location : locationList){
            location.stopUse();
        }
    }
    //재사용
    @Transactional
    public void reUseWarehouse(Long id){
        //창고 조회 (영속성 컨텍스트에 넣기)
        Warehouse warehouse = findById(id);
        //해당 엔티티 수정하고 트랜잭션 마무리
        //warehouse와 location 엔티티 수정
        warehouse.setUseYn(UseYn.Y);
        List<Location> locationList = locationRepository.findLocationsByWarehouse(id);
        for(Location location : locationList){
            location.reUse();
        }
    }

}
