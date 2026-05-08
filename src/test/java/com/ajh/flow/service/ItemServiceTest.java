package com.ajh.flow.service;

import com.ajh.flow.common.constant.ItemUnit;
import com.ajh.flow.common.exception.EntityNotFoundException;
import com.ajh.flow.domain.Item;
import com.ajh.flow.dto.item.ItemRegisterDto;
import com.ajh.flow.dto.item.ItemUpdateDto;
import com.ajh.flow.repository.ItemRepository;
import jakarta.persistence.EntityManager;
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
        ItemRegisterDto form = new ItemRegisterDto("사과",1000L,ItemUnit.EA,"두쫀쿠사과");
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
        ItemRegisterDto form1 = new ItemRegisterDto("사과",1000L,ItemUnit.EA,"두쫀쿠사과");
        ItemRegisterDto form2 = new ItemRegisterDto("배",2000L,ItemUnit.EA,"두쫀쿠배");
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

    //itemService.updateItem
    //수정이 정상적으로 반영되었는지
    @Test
    @DisplayName("변경감지를 이용한 수정이 정삭적으로 완료되어야 한다.")
    public void updateItem() throws Exception{
        //Given - 아이템 엔티티 하나를 먼저 db에 집어넣고 flush,clear
        Long itemId = itemService.registerItem(new ItemRegisterDto("사과",1000L,ItemUnit.EA,"두쫀쿠사과"));
        em.flush();
        em.clear();
        //When - 다시 엔티티를 영속성컨텍스트에 넣은다음 값 수정후 flush,clear
        Item item1 = itemService.findById(itemId);
        item1.update(new ItemUpdateDto("사과",1200L,ItemUnit.BOX,"봄동사과"));
        em.flush();
        em.clear();
        //Then
        Item item2 = itemService.findById(itemId);
        assertThat(item2.getPrice()).isEqualTo(1200L);
        assertThat(item2.getUnit()).isEqualTo(ItemUnit.BOX);
        assertThat(item2.getDescription()).isEqualTo("봄동사과");
    }
}