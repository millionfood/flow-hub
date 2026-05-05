package com.ajh.flow.common.constant;

import lombok.Getter;

@Getter
public enum UseYn {
    Y("사용"),
    N("미사용");

    private final String description;

    UseYn(String description) {
        this.description = description;
    }
}
