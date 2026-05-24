package com.ajh.flow.common.constant;

import lombok.Getter;

@Getter
public enum StockTransactionType {
    IN("입고"),
    OUT("출고"),
    MOVE("이동"),
    ADJ("재고조정"),
    RETURN("반품"),
    DELETE("폐기");

    private final String description;

    StockTransactionType(String description) {
        this.description = description;
    }
}
