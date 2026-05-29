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

    @NotBlank
    private String name;
    @NotBlank
    private String address;

}
