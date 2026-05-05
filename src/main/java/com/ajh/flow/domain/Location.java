package com.ajh.flow.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false, length = 50)
    private String locCode; // 로케이션 식별 코드

    @Column(length = 20)
    private String zone; //구역 (냉장,냉동,상온,위험물)

    @Column(nullable = false)
    private boolean useYn =  true;

    @Builder
    public Location(Warehouse warehouse, String locCode, String zone) {
        this.warehouse = warehouse;
        this.locCode = locCode;
        this.zone = zone;
    }

}
