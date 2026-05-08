package com.ajh.flow.repository;

import com.ajh.flow.domain.Item;
import com.ajh.flow.domain.Stock;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ItemRepository {

    private final EntityManager em;

    //저장
    public void save(Item item) {
        em.persist(item);
    }
    //단건조회 - 아이디 기준
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(em.find(Item.class, id));
    }
    //단건조회 - 바코드 기준
    public Optional<Item> findByBarcode(String barcode){
        return em.createQuery("select i from Item i where i.barcode = :barcode",Item.class)
                .setParameter("barcode",barcode)
                .getResultStream().findFirst();
    }
    //단건조회 - 마지막 등록된 상품 코드
    public String findLastProductCode(){
        String Barcode = em.createQuery(
                "select max(i.barcode) from Item i", String.class
        ).getSingleResult();
        if(Barcode == null){
            return "00000";
        }

        return Barcode.substring(7,12);

    }
    //단건 조회 - 이미 등록된 바코드가 있는지
    public boolean existsByBarcode(String barcode){
        Long count = em.createQuery("select count(i) from Item i where i.barcode =:barcode", Long.class)
                .setParameter("barcode",barcode)
                .getSingleResult();
        return count > 0;
    }
    //전체조회
    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }

    //상품 정보 수정 - 변경감지를 이용해 처리(service계층에서)

}
