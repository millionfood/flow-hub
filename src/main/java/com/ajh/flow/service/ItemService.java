package com.ajh.flow.service;

import com.ajh.flow.common.exception.EntityNotFoundException;
import com.ajh.flow.common.exception.InvalidBarcodeException;
import com.ajh.flow.domain.Item;
import com.ajh.flow.dto.item.ItemDetailDto;
import com.ajh.flow.dto.item.ItemRegisterDto;
import com.ajh.flow.dto.item.ItemUpdateDto;
import com.ajh.flow.repository.ItemRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final EntityManager em;

    //상품 등록
    @Transactional
    public Long registerItem(ItemRegisterDto form) {
        Item item = form.toVO();
        // barcode를 위해 db에서 가장 마지막에 등록된 상품 코드를 조회
        String productCode = itemRepository.findLastProductCode();
        item.createFullBarcode(productCode);

        //바코드 번호 중복체크 1차 방어 - 두 사용자가 같은 상품 코드 조회
        String barcode = item.getBarcode();
        if(itemRepository.existsByBarcode(barcode)){
            throw new InvalidBarcodeException("해당 바코드가 이미 존재합니다.");
        }
        //바코드 번호 중복체크 2차 방어 - 두 사용자가 동시에 등록(db 측에서 막아야함)
        try {
            itemRepository.save(item);
            em.flush(); //바코드 번호 중복 체크를 위해 강제 flush - 원래는 transaction이 끝날때 flush되기때문

        }catch (DataIntegrityViolationException e){
            throw new InvalidBarcodeException("해당 바코드는 방금 다른 사용자에 의해 등록되었습니다.");
        }

        return item.getId();
    }

    //-----------------조회-------------------
    //상품 전체 조회
    public List<ItemDetailDto> findAll() {
        return itemRepository.findAll().stream()
                .map(ItemDetailDto::new)
                .collect(Collectors.toList());
    }

    //상품 단건 조회 - 아이디
    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    //상품 단건 조회 - 바코드
    public Item findByBarcode(String barcode) {
        return  itemRepository.findByBarcode(barcode)
                .orElseThrow(EntityNotFoundException::new);
    }

    //-----------------수정-------------------
    @Transactional
    public void updateItem(Long id, ItemUpdateDto dto){
        //아이템 조회 (영속성 컨텍스트에 넣기)
        Item item = itemRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        //해당 엔티티 수정하고 트랜잭션 마무리
        item.update(dto);
    }

    //-----------------삭제-------------------
    @Transactional
    public void deleteItem(Long id){
        itemRepository.deleteById(id);
    }
}
