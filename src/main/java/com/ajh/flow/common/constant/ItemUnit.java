package com.ajh.flow.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemUnit {
    EA("개"),
    BOX("박스"),
    KG("킬로그램"),
    G("그램"),
    ML("밀리리터"),
    L("리터");

    private final String description;

}
