package com.ajh.flow.dto.stock;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.StockStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter@ToString
public class StockSearchCond {
    private Long warehouseId;    // 위치 창고 ID 필터 (null 이면 전체 창고)
    private String locCode;   // 전체("") / SHORTAGE(부족) / OUT_OF_STOCK(품절) / SUFFICIENT(정상)
    private LocationZone locationZone;
    private StockStatus stockStatus;
}
