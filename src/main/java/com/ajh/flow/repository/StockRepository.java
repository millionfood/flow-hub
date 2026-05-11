package com.ajh.flow.repository;

import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.domain.Item;
import com.ajh.flow.domain.Stock;
import com.ajh.flow.dto.stock.StockDetailDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StockRepository {

    private final EntityManager em;

    //-----------------등록-----------------
    public void save(Stock stock) {
        em.persist(stock);
    }

    //-----------------조회-----------------
    //전체 조회
    public List<StockDetailDto> findAll() {
        String jpql = "select new com.ajh.flow.dto.stock.StockDetailDto("+
                "s.id,w.name,l.id,l.locCode,i.id,i.name,s.quantity,s.lastModifiedDate,s.status,s.useYn) "+
                "from Stock s "+
                "join s.location l "+
                "join l.warehouse w "+
                "join s.item i "+
                "where s.useYn = 'Y'";
        return em.createQuery(jpql, StockDetailDto.class).getResultList();
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
    public boolean existsByLocationAndItem(Long locationId, Long itemId, StockStatus status) {
        Long count = em.createQuery("select count(s) from Stock s "+
                "where s.location.id =: locationId "+
                "and s.item.id =: itemId "+
                "and s.status =: status", Long.class)
                .setParameter("locationId",locationId)
                .setParameter("itemId",itemId)
                .setParameter("status",status)
                .getSingleResult();

        return count > 0;
    }
}
