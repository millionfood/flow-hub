package com.ajh.flow.domain;

import com.ajh.flow.common.constant.ItemUnit;
import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.dto.item.ItemUpdateDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Item extends BaseEntity {

    private static String PREFIX = "8801111"; // 한국880 + 자회사1111

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String barcode; //상품 식별자

    @Column(nullable = false)
    private Long price = 0L;

    @Column(nullable = false, length = 100)
    private String name; //상품명

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ItemUnit unit; //단위

    @Column(length = 255)
    private String description; //상품설명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UseYn useYn = UseYn.Y; // 사용 여부 (삭제 대신 상태값 변경)

    @Builder
    public Item(String barcode, Long price, String name, ItemUnit unit, String description) {
        this.barcode = barcode;
        this.price = price;
        this.name = name;
        this.unit = unit;
        this.description = description;
    }

    public void update(ItemUpdateDto itemUpdateDto) {
        this.name = itemUpdateDto.getName();
        this.price = itemUpdateDto.getPrice();
        this.unit = itemUpdateDto.getUnit();
        this.description = itemUpdateDto.getDescription();
    }

    public void createFullBarcode(String productCode){
        int nextCode = Integer.parseInt(productCode) + 1;
        String formattedCode = String.format("%05d", nextCode);
        String raw = PREFIX + formattedCode;
        int checkDigit = calculateEan13CheckDigit(raw);
        this.barcode = raw+checkDigit;
    }

    private int calculateEan13CheckDigit(String raw){
        int sum = 0;
        for(int i = 0; i < 12; i++){
            int digit = raw.charAt(i) - '0';
            if(i%2 ==  1){
                sum += digit * 3;
            }else{
                sum += digit;
            }
        }
        int remainder = sum % 10;

        return (10-remainder) %10;
    }
}
