package com.ajh.flow.dto.stock;

import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.domain.Stock;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class StockUpdateDto {

    public StockUpdateDto(Long quantity, StockStatus status) {
        this.quantity = quantity;
        this.status = status;
    }
    public StockUpdateDto(Stock stock) {
        this.quantity = stock.getQuantity();
        this.status = stock.getStatus();
    }

    private Long quantity;
    private StockStatus status;
}
