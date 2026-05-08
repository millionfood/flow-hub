package com.ajh.flow.dto.item;

import com.ajh.flow.common.constant.ItemUnit;
import com.ajh.flow.domain.Item;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemRegisterDto {

    public ItemRegisterDto(String name, Long price, ItemUnit unit, String description) {
        this.price = price;
        this.name = name;
        this.unit = unit;
        this.description = description;
    }

    @NotNull(message = "가격은 필수 입력 항목입니다.")
    private Long price;

    @NotNull(message = "상품명은 필수 입력 항목입니다.")
    private String name;

    @NotNull(message = "단위는 필수 입력 항목입니다.")
    private ItemUnit unit;

    private String description ="";

    public Item toVO(){
        return Item.builder()
                .price(this.price)
                .name(this.name)
                .unit(this.unit)
                .description(this.description)
                .build();
    }
}
