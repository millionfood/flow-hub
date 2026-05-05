package com.ajh.flow.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String barcode; //상품 식별자

    @Column(nullable = false)
    private Long price = 0L;

    @Column(nullable = false, length = 100)
    private String name; //상품명

    @Column(length = 20)
    private String unit; //단위

    @Column(length = 255)
    private String description; //상품설명

    @Column(nullable = false)
    private boolean useYn = true; // 사용 여부 (삭제 대신 상태값 변경)
}
