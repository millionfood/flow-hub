package com.ajh.flow.dto.item;

import com.ajh.flow.common.constant.ItemUnit;
import com.ajh.flow.common.constant.StockStatus;
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
    //사용자페이지/관리자페이지 - 전체 확인
    public ItemDetailDto(Long itemId,String name, String barcode,
                         String description, Long price, ItemUnit unit,
                         LocalDateTime createdDate,Long totalQuantity ) {
        this.id = itemId;
        this.name = name;
        this.barcode = barcode;
        this.description = description;
        this.price = price;
        this.unit = unit;
        this.createdDate = createdDate;
        this.totalQuantity = totalQuantity;
    }
    //관리자페이지 - 해당 아이템 상세 조회
    public ItemDetailDto(Long itemId,String name, String barcode,
                         String description, Long price, ItemUnit unit,
                         LocalDateTime createdDate,Long totalQuantity,StockStatus status ) {
        this.id = itemId;
        this.name = name;
        this.barcode = barcode;
        this.description = description;
        this.price = price;
        this.unit = unit;
        this.createdDate = createdDate;
        this.totalQuantity = totalQuantity;
        this.status = status;
    }

    private Long id;

    private String barcode;

    private String name;

    private String description;

    private Long price;

    private StockStatus status;

    private Long totalQuantity;

    private ItemUnit unit;

    private LocalDateTime createdDate;

}
