package com.ajh.flow.domain;

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

    @Column(length = 20)
    private String tel; //창고 연락처

    @Column(length = 100)
    private String managerName;

    @Column(nullable = false)
    private boolean useYn = true; // 사용 여부

    @Builder
    public Warehouse(String name, String address, String tel, String managerName) {
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.managerName = managerName;
    }
}
