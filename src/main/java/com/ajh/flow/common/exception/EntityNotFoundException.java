package com.ajh.flow.common.exception;

public class EntityNotFoundException extends BusinessException{
    //조회 실패 - 존재하지 않는 Item_id, Warehouse_id, Location_id
    public EntityNotFoundException() {
        super("존재하지 않는 객체입니다.");
    }
    public EntityNotFoundException(String message) {
        super("존재하지 않는 객체입니다. - 상세정보 :" + message);
    }
}
