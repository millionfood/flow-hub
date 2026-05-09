package com.ajh.flow.domain;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.UseYn;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "locations")
@Getter
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

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private LocationZone zone; //구역 (냉장,냉동,상온,위험물)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UseYn useYn =  UseYn.Y;

    @Column(name = "loc_row",nullable = false,length = 20,unique = true)
    private String row; //행
    @Column(name = "loc_col",nullable = false,length = 20,unique = true)
    private String col; //열
    @Column(name = "loc_level",nullable = false,length = 20,unique = true)
    private String level; //단

    @Builder
    public Location(Warehouse warehouse, String row,String col, String level, LocationZone zone) {
        this.warehouse = warehouse;
        this.row = row;
        this.col = col;
        this.level = level;
        this.zone = zone;
        this.locCode = String.format("%s-%02d-%02d-%02d",
                zone.getPrefix(),
                Integer.parseInt(row),
                Integer.parseInt(col),
                Integer.parseInt(level));
    }

    public void insertWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

}
