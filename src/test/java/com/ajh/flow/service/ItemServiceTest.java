package com.ajh.flow.service;

import com.ajh.flow.common.constant.ItemUnit;
import com.ajh.flow.common.exception.EntityNotFoundException;
import com.ajh.flow.common.exception.StockNotFoundException;
import com.ajh.flow.domain.Item;
import com.ajh.flow.dto.ItemRegisterForm;
import com.ajh.flow.repository.ItemRepository;
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
class ItemServiceTest {

    @Autowired ItemService itemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    EntityManager em;

    //itemService.registerItem
    //상품 등록 확인 - 한개의 Item도 없을때에 barcode를 잘 넣을 수 있는지
    @Test
    @DisplayName("상품 등록이 정상적으로 되었는지 확인")
    public void registerItem() throws Exception{
        //Given
        ItemRegisterForm form = new ItemRegisterForm(1000L,"사과",ItemUnit.EA,"두쫀쿠사과");
        //When
        Long savedItemId = itemService.registerItem(form);
        em.flush();
        em.clear();
        //Then
        Item findItem = itemRepository.findById(savedItemId)
                .orElseThrow(EntityNotFoundException::new);
        assertThat(findItem.getName()).isEqualTo(form.getName());
    }

    //itemService.registerItem
    // 바코드 번호가 순차적으로 부여되는지 확인 (시작 : 00000)
    @Test
    @DisplayName("바코드 번호는 순차적으로 부여 되어야 한다.")
    public void registerItemAndCheckBarcode() throws Exception{
        //Given
        ItemRegisterForm form1 = new ItemRegisterForm(1000L,"사과",ItemUnit.EA,"두쫀쿠사과");
        ItemRegisterForm form2 = new ItemRegisterForm(2000L,"배",ItemUnit.EA,"두쫀쿠배");
        //When
        itemService.registerItem(form1);
        em.flush();
        em.clear();
        assertThat(itemRepository.findLastProductCode()).isEqualTo("00001");

        itemService.registerItem(form2);
        em.flush();
        em.clear();
        assertThat(itemRepository.findLastProductCode()).isEqualTo("00002");
        //Then

    }

    //itemService.findById
    //잘못된 아이디,바코드를 입력했을 경우 EntityNotFoundException 이 발생하는지
    @Test
    @DisplayName("잘못된 아이디, 바코드 입력시 EntityNotFoundException이 발생해야 한다.")
    public void findEmptyItem(){
        assertThrows(EntityNotFoundException.class, () -> itemService.findById(10L));
        assertThrows(EntityNotFoundException.class, () -> itemService.findByBarcode("0000"));
    }


}