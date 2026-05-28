package com.ajh.flow.dto.user;

import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.common.constant.UserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter@ToString
public class UserSearchCond {

    private UseYn useYn;  // 전체("") / ACTIVE / BLOCK
    private UserRole role;    // 전체("") / USER / ADMIN
    private String keyword; // 이름, 이메일 통합 검색 키워드
}
