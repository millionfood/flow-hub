package com.ajh.flow.common.exception;

public class StockNotFoundException extends BusinessException {
    //재고 정보 없음
    public StockNotFoundException() {
        super("해당 재고에 대한 정보가 없습니다.");
    }
    public StockNotFoundException(String message) {
        super("해당 재고에 대한 정보가 없습니다. - 상세정보 :" + message);
    }
}
