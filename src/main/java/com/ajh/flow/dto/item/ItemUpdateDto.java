package com.ajh.flow.dto.item;

import com.ajh.flow.common.constant.ItemUnit;
import com.ajh.flow.domain.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemUpdateDto {

    public ItemUpdateDto(String name, Long price, ItemUnit unit, String description) {
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.description = description;
    }
    public ItemUpdateDto(Item item) {
        this.name = item.getName();
        this.price = item.getPrice();
        this.unit = item.getUnit();
        this.description = item.getDescription();
    }

    @NotBlank(message = "상품명은 필수입니다.")
    private String name;
    @NotNull(message = "가격은 필수입니다.")
    private Long price;
    @NotNull(message = "단위는 필수입니다.")
    private ItemUnit unit;
    private String description = "";

}
