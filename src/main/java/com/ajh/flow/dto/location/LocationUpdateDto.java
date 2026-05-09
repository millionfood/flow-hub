package com.ajh.flow.dto.location;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.domain.Location;
import com.ajh.flow.domain.Warehouse;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//현재 이 dto는 미사용입니다.
public class LocationUpdateDto {
    public LocationUpdateDto(Long warehouseId, Location location){
        this.warehouseId = warehouseId;
        this.zone =  location.getZone();
        this.row = location.getRow();
        this.col = location.getCol();
        this.level = location.getLevel();
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
    @NotNull(message = "locCode는 필수 입력값입니다.")
    private String locCode;

}
