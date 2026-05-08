package com.ajh.flow.dto.warehouse;

import com.ajh.flow.domain.Warehouse;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WarehouseRegisterDto {

    public WarehouseRegisterDto(String name,String address, String managerName,String tel) {
        this.name = name;
        this.address = address;
        this.managerName = managerName;
        this.tel = tel;
    }

    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotBlank
    private String managerName;
    @NotBlank
    private String tel;


    public Warehouse toVo() {
        return Warehouse.builder()
                .name(this.name)
                .address(this.address)
                .tel(this.tel)
                .managerName(this.managerName)
                .build();
    }

}
