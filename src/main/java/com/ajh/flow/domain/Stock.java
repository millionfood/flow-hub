package com.ajh.flow.domain;

import com.ajh.flow.common.exception.InsufficientStockException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "stocks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_location_item",
                        columnNames = {"location_id","item_id"}
                )
        }
)
@Getter @Setter
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

    @Builder
    public Stock( Location location, Item item, Long quantity) {
        this.location = location;
        this.item = item;
        this.quantity = quantity;
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
