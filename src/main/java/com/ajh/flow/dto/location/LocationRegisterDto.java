package com.ajh.flow.dto.location;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.domain.Location;
import com.ajh.flow.domain.Warehouse;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LocationRegisterDto {

    public LocationRegisterDto(Long warehouseId, LocationZone zone,String row, String col, String level) {
       this.warehouseId = warehouseId;
       this.zone = zone;
       this.row = row;
       this.col = col;
       this.level = level;
    }

    @NotNull(message = "창고 id는 필수입력값입니다.")
    private Long warehouseId;
    @NotNull(message = "구역(zone)은 필수 입력값입니다.")
    private LocationZone zone;
    @NotNull(message = "행(row)은 필수 입력값입니다.")
    private String row;
    @NotNull(message = "열(col)은 필수 입력 값입니다.")
    private String col;
    @NotNull(message = "단(level)은 필수 입력값입니다.")
    private String level;

    public Location toVo(){
        return Location.builder()
                .row(this.row)
                .col(this.col)
                .level(this.level)
                .zone(this.zone)
                .build();
    }

}
