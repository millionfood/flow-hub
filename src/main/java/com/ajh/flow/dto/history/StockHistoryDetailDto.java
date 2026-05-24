package com.ajh.flow.dto.history;

import com.ajh.flow.common.constant.StockTransactionType;
import com.ajh.flow.domain.StockHistory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.format.DateTimeFormatter;

@Getter@Setter
@NoArgsConstructor
public class StockHistoryDetailDto {

    public StockHistoryDetailDto(StockHistory history) {
        this.id = history.getId();
        this.itemId = history.getItem().getId();
        this.itemName = history.getItem().getName();
        this.itemBarcode = history.getItem().getBarcode();
        this.userId = history.getUser().getId();
        this.userName = history.getUser().getName();
        this.locationId = history.getLocation().getId();
        this.locationLocCode = history.getLocation().getLocCode();
        this.warehouseId = history.getWarehouse().getId();
        this.warehouseName = history.getWarehouse().getName();
        this.preQuantity = history.getPreQuantity();
        this.postQuantity = history.getPostQuantity();
        this.moveQuantity = history.getMoveQuantity();
        this.type = history.getType().name();
        this.remark = history.getRemark();
        this.createdDateStr = history.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.status = history.getStockStatus().name();
    }

    private Long id;

    private Long itemId;
    private String itemName;
    private String itemBarcode;
    private String status;

    private Long userId;
    private String userName;

    private Long locationId;
    private String locationLocCode;

    private Long warehouseId;
    private String warehouseName;

    private Long preQuantity;
    private Long postQuantity;
    private Long moveQuantity;

    private String remark;
    private String type;
    private String createdDateStr;





}
