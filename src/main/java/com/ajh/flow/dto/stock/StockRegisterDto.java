package com.ajh.flow.dto.stock;

import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.domain.Stock;
import com.ajh.flow.domain.Warehouse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockRegisterDto {

    public StockRegisterDto(Long warehouseId,Long itemId, Long locationId, Long quantity, StockStatus status) {
        this.warehouseId = warehouseId;
        this.itemId = itemId;
        this.locationId = locationId;
        this.quantity = quantity;
        this.status = status;
    }

    @NotNull
    private Long warehouseId;

    @NotNull(message = "상품은 필수 선택입니다.")
    private Long itemId;

    @NotNull(message = "로케이션은 필수 선택입니다.")
    private Long locationId;

    @Min(value = 1, message = "입고 수량은 최소 1개 이상이어야 합니다.")
    private Long quantity;

    @NotNull(message = "상품 상태는 필수 선택입니다.")
    private StockStatus status;

}
