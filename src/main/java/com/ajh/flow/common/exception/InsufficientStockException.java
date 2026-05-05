package com.ajh.flow.common.exception;

public class InsufficientStockException extends BusinessException{
    //재고 부족
    public InsufficientStockException() {
        super("재고가 부족합니다.");
    }
    public InsufficientStockException(String message) {
        super("재고가 부족합니다. - 상세정보 :" + message);
    }
}
