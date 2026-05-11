package com.ajh.flow.common.exception;

public class InvalidStockException extends BusinessException {
    public InvalidStockException(){
        super("해당 Stock 객체는 올바르지 않습니다.");
    }
    public InvalidStockException(String message) {
        super("해당 Stock 객체는 올바르지 않습니다. - 상세정보 :"+message);
    }
}
