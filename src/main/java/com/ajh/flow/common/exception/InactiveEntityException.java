package com.ajh.flow.common.exception;

public class InactiveEntityException extends BusinessException{
    //사용 불가 상태 - useYn 이 false인 창고,상품 등
    public InactiveEntityException() {
        super("사용할 수 없는 상태입니다.");
    }
    public InactiveEntityException(String message) {
        super("사용할 수 없는 상태입니다. - 상세정보 :" + message);
    }
}
