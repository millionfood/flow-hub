package com.ajh.flow.service;

import com.ajh.flow.common.constant.ItemUnit;
import com.ajh.flow.domain.Item;
import com.ajh.flow.dto.item.ItemRegisterDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemEntityTest {
    @Test
    @DisplayName("아이템 엔티티의 바코드가 정상적으로 생성되는지 확인")
    public void barcodeCalculationTest() throws Exception{
        //Given
        Item item = new ItemRegisterDto("사과",1000L, ItemUnit.EA,"두쫀쿠사과").toVO();
        String productCode = "00042";
        //When
        item.createFullBarcode(productCode);

        //Then
        assertThat(item.getBarcode()).isEqualTo("8801111000437");

    }
}
