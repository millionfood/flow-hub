package com.ajh.flow.common.exception;

public class DuplicateEntityException extends BusinessException {
    public DuplicateEntityException() {
        super("중복되는 객체입니다.");
    }
    public DuplicateEntityException(String message) {
        super("중복되는 객체입니다. - 상세정보 :" + message);
    }
}
