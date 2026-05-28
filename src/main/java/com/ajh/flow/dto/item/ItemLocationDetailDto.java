package com.ajh.flow.dto.item;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter@AllArgsConstructor
public class ItemLocationDetailDto {

    private Long warehouseId;
    private String warehouseName;

    private Long locationId;
    private LocationZone locationZone;
    private String locCode;

    private Long itemId;
    private String itemName;
    private String barcode;

    private StockStatus stockStatus;
    private Long quantity;
}