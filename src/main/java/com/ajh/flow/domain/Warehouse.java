package com.ajh.flow.domain;

import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.dto.warehouse.WarehouseUpdateDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Warehouse extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 255)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "register_id", nullable = false)
    private User register;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UseYn useYn = UseYn.Y; // 사용 여부

    @Builder
    public Warehouse(String name, String address, String tel, User register) {
        this.name = name;
        this.address = address;
        this.register = register;
    }

    public void update(WarehouseUpdateDto dto){
        this.name = dto.getName();
        this.address = dto.getAddress();
    }
}
