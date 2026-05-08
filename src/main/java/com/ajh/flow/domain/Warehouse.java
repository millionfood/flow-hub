package com.ajh.flow.domain;

import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.dto.warehouse.WarehouseUpdateDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @Column(length = 100)
    private String managerName;

    @Column(length = 20)
    private String tel; //창고 연락처

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UseYn useYn = UseYn.Y; // 사용 여부

    @Builder
    public Warehouse(String name, String address, String tel, String managerName) {
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.managerName = managerName;
    }

    public void update(WarehouseUpdateDto dto){
        this.name = dto.getName();
        this.address = dto.getAddress();
        this.managerName = dto.getManagerName();
        this.tel = dto.getTel();
    }
}
