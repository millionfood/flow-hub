package com.ajh.flow.dto.warehouse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WarehouseSearchCond {
    private String warehouseSearch; // 창고명
    private String adminName;
    private String adminTel;
}
