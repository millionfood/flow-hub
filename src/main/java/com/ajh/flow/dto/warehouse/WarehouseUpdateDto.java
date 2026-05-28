package com.ajh.flow.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class WarehouseUpdateDto {

    public WarehouseUpdateDto(String name, String address){
        this.name = name;
        this.address = address;
    }

    public WarehouseUpdateDto(WarehouseDetailDto dto) {
        this.name = dto.getName();
        this.address = dto.getAddress();
        this.registerName = dto.getRegisterName();
        this.registerTel = dto.getRegisterTel();
    }

    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotBlank
    private String registerName;
    @NotBlank
    private String registerTel;

}
