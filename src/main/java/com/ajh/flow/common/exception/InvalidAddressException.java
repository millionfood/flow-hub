package com.ajh.flow.common.exception;

public class InvalidAddressException extends BusinessException {
    public InvalidAddressException() {
        super("해당 주소는 올바르지 않습니다.");
    }

    public InvalidAddressException(String message) {
        super("해당 주소는 올바르지 않습니다. - 상세정보 :"+message);
    }
}
