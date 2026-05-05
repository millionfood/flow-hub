package com.ajh.flow.domain;

import com.ajh.flow.domain.stock.StockTransactionType;
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
public class History extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private Long quantity = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StockTransactionType type; // 작업 구분 (IN, OUT, MOVE, ADJ)

    @Column(length = 255)
    private String remark; //비고 (ex - 발주번호 0000-000 입고)

    @Builder
    public History(Item item, Location location,String remark, Long quantity, StockTransactionType type) {
        this.item = item;
        this.location = location;
        this.quantity = quantity;
        this.remark = remark;
        this.type = type;

    }
}
