package com.ajh.flow.domain;

import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.common.constant.StockTransactionType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stock_histories")
@Getter
@Setter
@NoArgsConstructor
public class StockHistory extends BaseEntity {

    public static StockHistory createStockHistory(Stock stock, User user,Long preQuantity ,Long moveQuantity,
                                                  StockTransactionType type, String remark) {

        Long postQuantity = preQuantity + moveQuantity;

        return StockHistory.builder()
                .item(stock.getItem())
                .warehouse(stock.getLocation().getWarehouse())
                .location(stock.getLocation())
                .user(user)
                .moveQuantity(moveQuantity)
                .preQuantity(preQuantity)
                .postQuantity(postQuantity)
                .remark(remark)
                .type(type)
                .stockStatus(stock.getStatus())
                .build();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "item_status")
    @Enumerated(EnumType.STRING)
    private StockStatus stockStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse ;

    @Column(nullable = false)
    private Long moveQuantity = 0L;

    @Column(nullable = false)
    private Long preQuantity = 0L;

    @Column(nullable = false)
    private Long postQuantity = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StockTransactionType type; // 작업 구분 (IN, OUT, MOVE, ADJ)

    @Column(length = 255)
    private String remark; //비고 (ex - 발주번호 0000-000 입고)

    @Builder
    public StockHistory(Item item,Warehouse warehouse, Location location,User user, String remark, Long moveQuantity,Long preQuantity,Long postQuantity, StockTransactionType type, StockStatus stockStatus) {
        this.item = item;
        this.warehouse = warehouse;
        this.location = location;
        this.user = user;
        this.moveQuantity = moveQuantity;
        this.preQuantity = preQuantity;
        this.postQuantity = postQuantity;
        this.remark = remark;
        this.type = type;
        this.stockStatus = stockStatus;

    }
}
