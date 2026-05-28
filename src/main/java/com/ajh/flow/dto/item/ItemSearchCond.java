package com.ajh.flow.dto.item;

import com.ajh.flow.common.constant.StockStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter@ToString
public class ItemSearchCond {
    private StockStatus status;   // 전체("") / ON_SALE / SUSPENDED / HIDDEN
    private String itemKeyword;
}
