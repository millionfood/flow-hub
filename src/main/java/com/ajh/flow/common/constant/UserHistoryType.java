package com.ajh.flow.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserHistoryType {
    DISABLE("계정정지"),
    ENABLE("계정활성화");

    private final String description;

}
