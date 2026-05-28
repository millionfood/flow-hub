package com.ajh.flow.repository;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.domain.Item;
import com.ajh.flow.domain.Location;
import com.ajh.flow.dto.item.ItemDetailDto;
import com.ajh.flow.dto.location.LocationDetailDto;
import com.ajh.flow.dto.location.LocationSearchCond;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.ajh.flow.domain.QLocation.location;

@Repository
@RequiredArgsConstructor
public class LocationRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;


    //-----------------등록-----------------
    public void save(Location location) {
        em.persist(location);
    }


    //-----------------조회-----------------
    //전체 조회
    public List<Location> findAll(){
        return em.createQuery("select l from Location l",Location.class).getResultList();
    }
    //전체리스트 - 검색,페이징
    public Page<Location> findAllWithPaging(Pageable pageable, LocationSearchCond cond) {

        List<Location> content = queryFactory
                .selectFrom(location)
                .where(
                        locCodeLike(cond.getLocCode()),
                        warehouseIdEq(cond.getWarehouseId()),
                        zoneEq(cond.getZone())
                )
                .orderBy(location.locCode.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(location.count())
                .from(location)
                .where(
                        locCodeLike(cond.getLocCode()),
                        warehouseIdEq(cond.getWarehouseId()),
                        zoneEq(cond.getZone())
                )
                .fetchOne();
        long total = totalCount != null ? totalCount : 0L;

        return new PageImpl<>(content, pageable, total);

    }
    //상품 등록시 입고 가능한 로케이션 목록
    public List<Location> findInboundAbleALlLocation(Long itemId, Long warehouseId) {
        String jpql = "select l from Location l "+
                "where l not in ("+
                "select s.location from Stock s "+
                "where s.item.id != : itemId) "+
                "and l.warehouse.id = :warehouseId";
        return em.createQuery(jpql,Location.class)
                .setParameter("itemId",itemId)
                .setParameter("warehouseId",warehouseId)
                .getResultList();
    }
    //상품 등록시 창고 기준으로 입고 가능한 로케이션 목록
    public List<Location> findInboundAbleLocationByWarehouse(Long warehouseId){

        String jpql = "select l from Location l "+
                "where l.warehouse.id = :warehouseId";

        return em.createQuery(jpql,Location.class)
                .setParameter("warehouseId",warehouseId)
                .getResultList();

    }
    //로케이션 등록시 등록 가능한 로케이션 목록
    public List<String> findLevelsByZone_Row_Col(Long warehouseId, LocationZone zone, String row, String col){
        return em.createQuery(
        "select l.level from Location l " +
                "where l.warehouse.id = :warehouseId " +
                "and l.zone = :zone " +
                "and l.row = :row " +
                "and l.col = :col", String.class)
                .setParameter("warehouseId", warehouseId)
                .setParameter("zone", zone)
                .setParameter("row", row)
                .setParameter("col", col)
                .getResultList();
    }

    //단건 조회 - 아이디 기준
    public Optional<Location> findById(Long id) {
        return Optional.ofNullable(em.find(Location.class,id));
    }
    //동일한 locCode가 있는지 확인
    public boolean existsByLocCode(Long warehouseId, LocationZone zone, String locCode) {
        Long count = em.createQuery("select count(l) from Location l where l.warehouse.id =: warehouseId and l.locCode =:locCode", Long.class)
                .setParameter("warehouseId", warehouseId)
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
    //queryDsl 메서드
    private BooleanExpression locCodeLike(String locCode){
        return StringUtils.hasText(locCode) ? location.locCode.containsIgnoreCase(locCode.trim()) : null;
    }
    private BooleanExpression warehouseIdEq(Long warehouseId){
        return warehouseId != null ? location.warehouse.id.eq(warehouseId) : null;
    }
    private BooleanExpression zoneEq(LocationZone locationZone){
        return locationZone != null ? location.zone.eq(locationZone) : null;
    }
}
