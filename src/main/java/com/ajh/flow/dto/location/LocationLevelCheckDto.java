package com.ajh.flow.dto.location;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationLevelCheckDto {
    private String zone;
    private String row;
    private String col;
    private String level;
    private boolean isRegistered;
}
