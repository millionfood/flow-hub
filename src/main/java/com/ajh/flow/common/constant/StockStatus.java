package com.ajh.flow.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StockStatus {
    AVAILABLE("출고 가능"),
    HOLD("보류"),
    DAMAGED("불량");

    private final String description;
}