package com.ajh.flow.repository;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.domain.Item;
import com.ajh.flow.domain.Location;
import com.ajh.flow.dto.location.LocationDetailDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LocationRepository {

    private final EntityManager em;


    //-----------------등록-----------------
    public void save(Location location) {
        em.persist(location);
    }


    //-----------------조회-----------------
    //전체 조회
    public List<Location> findAll(){
        return em.createQuery("select l from Location l",Location.class).getResultList();
    }
    //단건 조회 - 아이디 기준
    public Optional<Location> findById(Long id) {
        return Optional.ofNullable(em.find(Location.class,id));
    }
    //동일한 locCode가 있는지 확인
    public boolean existsByLocCode(Long warehouseId, LocationZone zone, String locCode) {
        Long count = em.createQuery("select count(l) from Location l where l.warehouse.id =: warehouseId and l.zone =: zone and l.locCode =:locCode", Long.class)
                .setParameter("warehouseId", warehouseId)
                .setParameter("zone", zone)
                .setParameter("locCode",locCode)
                .getSingleResult();
        return count > 0;
    }
    //입고 가능한 로케이션(다른 상품이 있으면 안됨)
    //이동 가능한 로케이션(다른 상품이 있거나, 같은 상태의 동일한 상품이 있으면 안됨)
    public List<Location> findMoveableLocations(Item item,Location location){
        String jpql = "SELECT l FROM Location l "+
                "WHERE l != :location "+
                "AND l NOT IN ( "+
                "SELECT s.location FROM Stock s "+
                "WHERE s.item != :item)";
        return em.createQuery(jpql,Location.class)
                .setParameter("item",item)
                .setParameter("location",location)
                .getResultList();
    }

    //-----------------수정/상태변경-----------------
    //변경 감지를 이용


    //-----------------기타-----------------
}
