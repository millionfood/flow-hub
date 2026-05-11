package com.ajh.flow.common.exception;

public class InvalidLocationException extends BusinessException {
    public InvalidLocationException() {
        super("해당 Location 객체는 올바르지 않습니다.");
    }
    public InvalidLocationException(String message) {

        super("해당 Location 객체는 올바르지 않습니다. - 상세정보 :"+message);
    }
}
