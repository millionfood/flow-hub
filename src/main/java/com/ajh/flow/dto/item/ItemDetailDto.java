package com.ajh.flow.dto.item;

import com.ajh.flow.common.constant.ItemUnit;
import com.ajh.flow.domain.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ItemDetailDto {

    public ItemDetailDto(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.barcode = item.getBarcode();
        this.description = item.getDescription();
        this.price = item.getPrice();
        this.unit = item.getUnit();
        this.createdDate = item.getCreatedDate();
    }

    private Long id;

    private String barcode;

    private String name;

    private String description;

    private Long price;

    private ItemUnit unit;

    private LocalDateTime createdDate;

}
