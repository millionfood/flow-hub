package com.ajh.flow.dto.location;

import com.ajh.flow.common.constant.LocationZone;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter@ToString
public class LocationSearchCond {
    private Long warehouseId;
    private String locCode;
    private LocationZone zone;
    private String row;
    private String col;
}
