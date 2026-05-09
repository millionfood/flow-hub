package com.ajh.flow.dto.location;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.domain.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocationDetailDto {

    public LocationDetailDto(Location location){
        this.id = location.getId();
        this.warehouseName = location.getWarehouse().getName();
        this.zone =  location.getZone();
        this.locCode = location.getLocCode();
        this.useYn = location.getUseYn();
        this.row =  location.getRow();
        this.col =  location.getCol();
        this.level = location.getLevel();
    }
    private Long id;
    private String warehouseName;
    private String row;
    private String col;
    private String level;
    private LocationZone zone;
    private String locCode;
    private UseYn useYn;

}
