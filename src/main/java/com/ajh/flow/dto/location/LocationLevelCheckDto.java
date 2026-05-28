package com.ajh.flow.dto.location;

import com.ajh.flow.common.constant.LocationZone;
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
