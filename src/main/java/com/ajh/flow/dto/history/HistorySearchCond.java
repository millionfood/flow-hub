package com.ajh.flow.dto.history;

import com.ajh.flow.common.constant.StockTransactionType;
import com.ajh.flow.common.constant.UserHistoryType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter@Setter
public class HistorySearchCond {

    //공통 필드
    private String remarkKeyword;

    //유저 히스토리 전용 필드
    private UserHistoryType userType;
    private String adminSearch; //관리자 이름 or 이메일
    private String targetSearch; //대상 유저의 이름 or 이메일


    //재고 히스토리 전용 필드
    private StockTransactionType moveType;
    private String operatorName;
    private String itemSearch;
    private Long locationId;
    private Long warehouseId;

}
