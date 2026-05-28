package com.ajh.flow.dto.stock;

import com.ajh.flow.common.constant.StockStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class StockMoveDto {

    public StockMoveDto(Long itemId, Long toLocationId, Long moveQuantity, StockStatus status, String remark) {
        this.itemId = itemId;
        this.toLocationId = toLocationId;
        this.moveQuantity = moveQuantity;
        this.status = status;
        this.remark = remark;
    }

    public StockMoveDto(StockDetailDto dto){
        this.itemId = dto.getItemId();
        this.toLocationId = dto.getId();
        this.moveQuantity = dto.getQuantity();
        this.status = dto.getStatus();
    }

    @NotNull(message = "itemId는 필수 입력값입니다.")
    private Long itemId;
    @NotNull(message = "toLocationId 필수 입력값입니다.")
    private Long toLocationId;
    @NotNull(message = "moveQuantity 필수 입력값입니다.")
    private Long moveQuantity;
    @NotNull(message = "status 필수 입력값입니다.")
    private StockStatus status;
    @NotNull(message = "remark 필수 입력값입니다.")
    private String remark;
}
