package com.ajh.flow.dto.stock;

import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.domain.Stock;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockRegisterDto {

    public StockRegisterDto(Long itemId, Long locationId, Long quantity, StockStatus status) {
        this.itemId = itemId;
        this.locationId = locationId;
        this.quantity = quantity;
        this.status = status;
    }
    public StockRegisterDto(StockMoveDto dto){
        this.itemId = dto.getItemId();
        this.locationId = dto.getToLocationId();
        this.quantity = dto.getMoveQuantity();
        this.status = dto.getStatus();
    }

    @NotNull(message = "상품은 필수 선택입니다.")
    private Long itemId;

    @NotNull(message = "로케이션은 필수 선택입니다.")
    private Long locationId;

    @Min(value = 1, message = "입고 수량은 최소 1개 이상이어야 합니다.")
    private Long quantity;

    @NotNull(message = "상품 상태는 필수 선택입니다.")
    private StockStatus status;

}
