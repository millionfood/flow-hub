package com.ajh.flow.dto.warehouse;

import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.domain.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDetailDto {

    public WarehouseDetailDto(Warehouse warehouse) {
        this.id = warehouse.getId();
        this.name = warehouse.getName();
        this.address = warehouse.getAddress();
        this.registerName = warehouse.getRegister().getName();
        this.registerTel = warehouse.getRegister().getTel();
        this.useYn = warehouse.getUseYn();
    }

    private Long id;
    private String name;
    private String address;
    private String registerName;
    private String registerTel;
    private UseYn useYn;
}
