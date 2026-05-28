package com.ajh.flow.repository;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.domain.Item;
import com.ajh.flow.domain.Stock;
import com.ajh.flow.dto.item.ItemLocationDetailDto;
import com.ajh.flow.dto.item.ItemLocationSearchCond;
import com.ajh.flow.dto.stock.StockDetailDto;
import com.ajh.flow.dto.stock.StockSearchCond;
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

import static com.ajh.flow.domain.QItem.item;
import static com.ajh.flow.domain.QLocation.location;
import static com.ajh.flow.domain.QStock.stock;
import static com.ajh.flow.domain.QWarehouse.warehouse;

@Repository
@RequiredArgsConstructor
public class StockRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    //-----------------등록-----------------
    public void save(Stock stock) {
        em.persist(stock);
    }

    //-----------------조회-----------------
    //전체 조회
    public List<StockDetailDto> findAll() {
        String jpql = "select new com.ajh.flow.dto.stock.StockDetailDto("+
                "s.id,w.name,l.id,l.locCode,l.zone,i.id,i.name,s.quantity,s.lastModifiedDate,s.status,s.useYn) "+
                "from Stock s "+
                "join s.location l "+
                "join l.warehouse w "+
                "join s.item i "+
                "where s.useYn = 'Y'";
        return em.createQuery(jpql, StockDetailDto.class).getResultList();
    }
    public Page<StockDetailDto> findAllDetailWithPaging(Pageable pageable,StockSearchCond cond) {
        List<StockDetailDto> content = queryFactory
                .select(Projections.constructor(StockDetailDto.class,
                        stock.id,
                        warehouse.name,
                        location.id,
                        location.locCode,
                        location.zone,
                        item.id,
                        item.name,
                        stock.quantity,
                        stock.lastModifiedDate,
                        stock.status,
                        stock.useYn))
                .from(stock)
                .join(stock.item,item)
                .join(stock.location, location)
                .join(stock.location.warehouse,warehouse)
                .where(
                        warehouseIdEq(cond.getWarehouseId()),
                        locCodeLike(cond.getLocCode()),
                        locationZoneEq(cond.getLocationZone()),
                        stockStatusEq(cond.getStockStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Long totalCount = queryFactory
                .select(stock.count())
                .from(stock)
                .join(stock.item,item)
                .join(stock.location, location)
                .join(stock.location.warehouse,warehouse)
                .where(
                        warehouseIdEq(cond.getWarehouseId()),
                        locCodeLike(cond.getLocCode()),
                        locationZoneEq(cond.getLocationZone()),
                        stockStatusEq(cond.getStockStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;

        return new PageImpl<>(content, pageable, total);
    }
    public Page<ItemLocationDetailDto> findAllWithPaging(Long itemId, Pageable pageable, ItemLocationSearchCond cond){
        List<ItemLocationDetailDto> content = queryFactory
                .select(Projections.constructor(ItemLocationDetailDto.class,
                        warehouse.id,
                        warehouse.name,
                        location.id,
                        location.zone,
                        location.locCode,
                        item.id,
                        item.name,
                        item.barcode,
                        stock.status,
                        stock.quantity.sum()
                        ))
                .from(stock)
                .join(stock.item,item)
                .join(stock.location,location)
                .join(location.warehouse,warehouse)
                .where(
                        stock.item.id.eq(itemId),
                        warehouseIdEq(cond.getWarehouseId()),
                        locCodeLike(cond.getLocCode()),
                        stockStatusEq(cond.getStockStatus()),
                        locationZoneEq(cond.getLocationZone())
                )
                .groupBy(
                        warehouse.id,
                        warehouse.name,
                        location.id,
                        location.zone,
                        location.locCode,
                        item.id,
                        item.name,
                        item.barcode,
                        stock.status
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(location.id.stringValue()
                        .concat("_")
                        .concat(item.id.stringValue())
                        .concat("_")
                        .concat(stock.status.stringValue())
                        .countDistinct())
                .from(stock)
                .join(stock.item,item)
                .join(stock.location, location)
                .join(location.warehouse, warehouse)
                .where(
                        warehouseIdEq(cond.getWarehouseId()),
                        locCodeLike(cond.getLocCode()),
                        stockStatusEq(cond.getStockStatus()),
                        locationZoneEq(cond.getLocationZone())
                )
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;

        return new PageImpl<>(content, pageable, total);

    }
    //단건 조회 - 엔티티
    public Optional<Stock> findById(Long id) {
        return Optional.ofNullable(em.find(Stock.class, id));
    }
    //단건 조회 - dto(수정 화면에서 보여줄 정보)
    public Optional<StockDetailDto> findDetailDtoById(Long id) {
        String jpql = "select new com.ajh.flow.dto.stock.StockDetailDto("+
                "s.id,w.name,l.id,l.locCode,i.id,i.name,s.quantity,s.lastModifiedDate,s.status,s.useYn) "+
                "from Stock s "+
                "join s.location l "+
                "join l.warehouse w "+
                "join s.item i "+
                "where s.id = :id";
        return em.createQuery(jpql,StockDetailDto.class).setParameter("id", id)
                .getResultList().stream().findFirst();
    }
    //단건 조회 - 로케이션 id와 아이템 id로 재고 조회
    public Optional<Stock> findByLocationAndItem(Long locationId, Long itemId) {
        String jpql = "SELECT s FROM Stock s " +
                      "WHERE s.location.id = :locationId " +
                      "AND s.item.id = :itemId";
        List<Stock> result = em.createQuery(jpql, Stock.class)
                .setParameter("locationId", locationId)
                .setParameter("itemId", itemId)
                .getResultList();

        return result.stream().findFirst();
    }
    //단건 조회 - 해당 로케이션에 재고가 있는지 확인
    public List<Stock> findByLocation(Long locationId) {
        String jpql = "SELECT s FROM Stock s "+
                "WHERE s.location.id = :locationId";
        return em.createQuery(jpql,Stock.class)
                .setParameter("locationId",locationId)
                .getResultList();
    }


    //-----------------수정-----------------
    //jpa의 변경 감지 활용
    //-----------------삭제-----------------
    public void delete(Long stockId) {
        em.remove(em.find(Stock.class, stockId));
    }
    //-----------------기타-----------------
    //특정 아이템의 전체 재고 합계
    public Long getTotalQuantity(Long itemId){
        String jpql = "SELECT SUM(s.quantity) FROM Stock s WHERE s.item.id = :itemId";

        Long result = em.createQuery(jpql, Long.class)
                .setParameter("itemId", itemId)
                .getSingleResult();
        return result != null ? result : 0L;
    }

    //특정 로케이션에 속한 아이템 목록
    public Optional<Stock> find_Same_Location_Item_Status(Long locationId, Long itemId, StockStatus status) {
        return em.createQuery("select s from Stock s "+
                "where s.location.id =: locationId "+
                "and s.item.id =: itemId "+
                "and s.status =: status", Stock.class)
                .setParameter("locationId",locationId)
                .setParameter("itemId",itemId)
                .setParameter("status",status)
                .getResultList().stream().findFirst();


    }
    //queryDsl 메서드
    // 1. 특정 창고 필터 (Long 일치 확인)
    private BooleanExpression warehouseIdEq(Long warehouseId) {
        return warehouseId != null ? warehouse.id.eq(warehouseId) : null;
    }

    // 2. 세부 로케이션 코드 필터 (포함 검색)
    private BooleanExpression locCodeLike(String locCode) {
        return StringUtils.hasText(locCode) ? location.locCode.containsIgnoreCase(locCode.trim()) : null;
    }

    // 3. 상품 재고 상태 필터 (Enum 일치 확인)
    private BooleanExpression stockStatusEq(StockStatus stockStatus) {
        return stockStatus != null ? stock.status.eq(stockStatus) : null;
    }

    // 4. 상품 Zone 필터 (Enum 일치 확인)
    private BooleanExpression locationZoneEq(LocationZone locationZone) {
        return locationZone != null ? location.zone.eq(locationZone) : null;
    }

    // 5. ⭐ 상품명 OR 바코드 통합 검색 필터 (가장 중요)
    private BooleanExpression stockSearchLike(String stockSearch) {
        if (!StringUtils.hasText(stockSearch)) {
            return null;
        }
        String likeKeyword = "%" + stockSearch + "%";
        return item.name.like(likeKeyword)
                .or(item.barcode.like(likeKeyword));
    }
}
