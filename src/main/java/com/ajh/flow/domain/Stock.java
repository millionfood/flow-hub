package com.ajh.flow.domain;

import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.common.exception.InsufficientStockException;
import com.ajh.flow.dto.stock.StockUpdateDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "stocks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_location_item",
                        columnNames = {"status","location_id","item_id"}
                )
        }
)
@Getter
@NoArgsConstructor
public class Stock extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Long quantity = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UseYn useYn = UseYn.Y;

    @Builder
    public Stock( Location location, Item item, Long quantity, StockStatus status) {
        this.location = location;
        this.item = item;
        this.quantity = quantity;
        this.status = status;
    }

    public void update(StockUpdateDto dto){
        this.quantity = dto.getQuantity();
        this.status = dto.getStatus();
    }

    //-----비즈니스 로직-----

    //재고 증가 (입고)
    public void addQuantity(Long amount) {
        this.quantity += amount;
    }

    //재고 감소 (출고)
    public void removeQuantity(Long amount) {
        if (this.quantity < amount) {
            throw new InsufficientStockException("(재고량) < (출고량)");
        }
        this.quantity -= amount;
    }


}
