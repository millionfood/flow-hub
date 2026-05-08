package com.ajh.flow.repository;

import com.ajh.flow.common.exception.EntityNotFoundException;
import com.ajh.flow.domain.Warehouse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WarehouseRepository {

    private final EntityManager em;

    //-----------------저장-----------------
    public void save(Warehouse warehouse) {
        em.persist(warehouse);
    }

    //-----------------조회-----------------
    //id 값으로 조회
    public Optional<Warehouse> findById(Long id) {
        return Optional.ofNullable(em.find(Warehouse.class,id));
    }
    //동일한 주소가 있는지 조회
    public boolean existsByAddress(String address) {
        Long count = em.createQuery("select count(w) from Warehouse w where w.address =:address",Long.class)
                .setParameter("address",address)
                .getSingleResult();
        return count > 0;
    }
    //전체 조회
    public List<Warehouse> findAll() {
        return em.createQuery("select w from Warehouse w", Warehouse.class).getResultList();
    }

    //-----------------수정-----------------
    //변경 감지를 통한 수정

    //-----------------삭제-----------------
    //변경 감지를 통한 수정(사용하지 않는 상태로 변경)
}
