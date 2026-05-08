package com.ajh.flow.dto.warehouse;

import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.domain.Warehouse;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WarehouseDetailDto {

    public WarehouseDetailDto(Warehouse warehouse) {
        this.id = warehouse.getId();
        this.name = warehouse.getName();
        this.address = warehouse.getAddress();
        this.managerName = warehouse.getManagerName();
        this.tel = warehouse.getTel();
        this.useYn = warehouse.getUseYn();
    }

    private Long id;
    private String name;
    private String address;
    private String managerName;
    private String tel;
    private UseYn useYn;
}
