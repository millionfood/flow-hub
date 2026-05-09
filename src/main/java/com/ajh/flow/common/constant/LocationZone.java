package com.ajh.flow.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum LocationZone {
    COLD("C", "냉동구역"),
    FRIDGE("F", "냉장구역"),
    ROOM("R", "상온구역"),
    HAZARD("H","위험구역");

    private final String prefix;
    private final String description;
}
