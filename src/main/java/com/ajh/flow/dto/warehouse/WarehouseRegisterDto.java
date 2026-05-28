package com.ajh.flow.dto.warehouse;

import com.ajh.flow.domain.Warehouse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WarehouseRegisterDto {

    public WarehouseRegisterDto(String name,String address, Long registerId) {
        this.name = name;
        this.address = address;
        this.registerId = registerId;
    }

    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotNull
    private Long registerId;



    public Warehouse toVo() {
        return Warehouse.builder()
                .name(this.name)
                .address(this.address)
                .build();
    }

}
