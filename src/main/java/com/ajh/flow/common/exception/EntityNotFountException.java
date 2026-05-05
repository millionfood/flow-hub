package com.ajh.flow.common.exception;

public class EntityNotFountException extends BusinessException{
    //조회 실패 - 존재하지 않는 Item_id, Warehouse_id, Location_id
    public EntityNotFountException( ) {
        super("존재하지 않는 객체입니다.");
    }
    public EntityNotFountException(String message) {
        super("존재하지 않는 객체입니다. - 상세정보 :" + message);
    }
}
