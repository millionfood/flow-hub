package com.ajh.flow.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    USER("일반회원"),
    ADMIN("관리자");
    private final String description;
}
