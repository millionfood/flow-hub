package com.ajh.flow.domain.stock;

public enum StockTransactionType {
    IN("입고"),
    OUT("출고"),
    MOVE("이동"),
    ADJ("재고조정"),
    RETURN("반품");

    private final String description;

    StockTransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
