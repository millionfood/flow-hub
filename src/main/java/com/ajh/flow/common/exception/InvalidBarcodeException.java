package com.ajh.flow.common.exception;

public class InvalidBarcodeException extends BusinessException{
    //바코드 오류 - 스캔한 바코드가 시스템에 등록되지 않았거나 형식이 잘못되었을 경우
    public InvalidBarcodeException() {
        super("해당 바코드는 올바르지 않습니다.");
    }
    public InvalidBarcodeException(String message) {
        super("해당 바코드는 올바르지 않습니다. - 상세정보 :"+message);
    }
}
