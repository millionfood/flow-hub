package com.ajh.flow.dto.warehouse;

import com.ajh.flow.domain.Warehouse;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class WarehouseUpdateDto {

    public WarehouseUpdateDto(String name, String address, String managerName, String tel){
        this.name = name;
        this.address = address;
        this.managerName = managerName;
        this.tel = tel;
    }

    public WarehouseUpdateDto(Warehouse warehouse) {
        this.name = warehouse.getName();
        this.address = warehouse.getAddress();
        this.managerName = warehouse.getManagerName();
        this.tel = warehouse.getTel();
    }

    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotBlank
    private String managerName;
    @NotBlank
    private String tel;

}
