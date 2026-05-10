package com.ajh.flow.dto.stock;

import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.domain.Stock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StockDetailDto {

    private Long id;
    private String warehouseName;
    private Long locationId;
    private String locationLocCode;
    private Long itemId;
    private String itemName;
    private Long quantity;
    private LocalDateTime lastModifiedDate;
    private StockStatus status;
    private UseYn useYn;


}
