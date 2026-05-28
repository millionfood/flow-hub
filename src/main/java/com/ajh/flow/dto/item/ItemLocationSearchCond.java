package com.ajh.flow.dto.item;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.StockStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter@ToString
public class ItemLocationSearchCond {
    private Long warehouseId;  // 특정 창고 필터
    private String locCode; //loc_code 필터
    private StockStatus stockStatus; //상품 상태 필터
    private LocationZone locationZone; //상품 zone 필터
}
