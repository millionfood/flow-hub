package com.ajh.flow.repository;

import com.ajh.flow.domain.StockHistory;
import com.ajh.flow.domain.UserHistory;
import com.ajh.flow.dto.history.HistorySearchCond;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HistoryRepository {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    //-----------------User-----------------
    public void saveUserHistory(UserHistory userHistory) {
        em.persist(userHistory);
    }

    public Page<UserHistory> findAllUserHistory(HistorySearchCond cond, Pageable pageable) {
        String jpql = "select h from UserHistory h "+
                "join fetch h.admin a " +
                "join fetch h.targetUser t "+
                "where 1=1";
        //조건에 다른 동적 JPQL 문자열 조립
        if(cond.getUserType() != null){
            jpql += " and h.type = :type";
        }
        if(StringUtils.hasText(cond.getAdminSearch())){
            jpql += " and (a.name like concat('%', :adminSearch, '%') or a.email like concat('%', :adminSearch, '%'))";
        }
        if(StringUtils.hasText(cond.getTargetSearch())){
            jpql += " and (t.name like concat('%', :targetSearch, '%') or t.email like concat('%', :targetSearch, '%'))";
        }
        if(StringUtils.hasText(cond.getRemarkKeyword())){
            jpql += " and h.remark like concat('%', :remarkKeyword, '%')";
        }
        //최신 로그 순 정렬
        jpql += " order by h.id desc";

        TypedQuery<UserHistory> query = em.createQuery(jpql, UserHistory.class);

        //조립된 쿼리에 파라미터 바인딩
        if(cond.getUserType() != null){
            query.setParameter("type", cond.getUserType());
        }
        if(StringUtils.hasText(cond.getAdminSearch())){
            query.setParameter("adminSearch", cond.getAdminSearch());
        }
        if(StringUtils.hasText(cond.getTargetSearch())){
            query.setParameter("targetSearch", cond.getTargetSearch());
        }
        if(StringUtils.hasText(cond.getRemarkKeyword())){
            query.setParameter("remarkKeyword", cond.getRemarkKeyword());
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<UserHistory> resultList = query.getResultList();

        String countJpql = "select count(h) from UserHistory h"+
                " join h.admin a join h.targetUser t where 1=1";
        if(cond.getUserType() != null) countJpql += " and h.type = :type";
        if(StringUtils.hasText(cond.getAdminSearch())) countJpql += " and (a.name like concat('%', :adminSearch, '%') or a.email like concat('%', :adminSearch, '%'))";
        if(StringUtils.hasText(cond.getTargetSearch())) countJpql += " and (t.name like concat('%', :targetSearch, '%') or t.email like concat('%', :targetSearch, '%'))";
        if(StringUtils.hasText(cond.getRemarkKeyword())) countJpql += " and h.remark like concat('%', :remarkKeyword, '%')";

        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

        if(cond.getUserType() != null) countQuery.setParameter("type", cond.getUserType());
        if(StringUtils.hasText(cond.getAdminSearch())) countQuery.setParameter("adminSearch", cond.getAdminSearch());
        if(StringUtils.hasText(cond.getTargetSearch())) countQuery.setParameter("targetSearch", cond.getTargetSearch());
        if(StringUtils.hasText(cond.getRemarkKeyword())) countQuery.setParameter("remarkKeyword", cond.getRemarkKeyword());

        Long totalCount = countQuery.getSingleResult();

        return new PageImpl<>(resultList, pageable, totalCount);

    }


    //-----------------Item-----------------
    public void saveStockHistory(StockHistory stockHistory) {
        em.persist(stockHistory);
    }

    public Page<StockHistory> findAllStockHistory(HistorySearchCond cond, Pageable pageable) {
        String jpql = "select h from StockHistory h "+
                "join fetch h.user u " +
                "join fetch h.item i " +
                "join fetch h.location l " +
                "join fetch h.warehouse w " +
                "where 1=1";
        //조건에 다른 동적 JPQL 문자열 조립
        if(cond.getMoveType() != null){
            jpql += " and h.type = :type";
        }
        if(StringUtils.hasText(cond.getOperatorName())){
            jpql += " and u.name like concat('%', :operatorName, '%')";
        }
        if(StringUtils.hasText(cond.getItemSearch())){
            jpql += " and (i.name like concat('%', :itemSearch, '%') or i.barcode like concat('%', :itemSearch, '%'))";
        }
        if(cond.getLocationId() != null){
            jpql += " and l.id = :locationId";
        }
        if(cond.getWarehouseId() != null){
            jpql += " and w.id = :warehouseId";
        }
        //최신 로그 순 정렬
        jpql += " order by h.id desc";

        TypedQuery<StockHistory> query = em.createQuery(jpql, StockHistory.class);

        //조립된 쿼리에 파라미터 바인딩
        if(cond.getMoveType() != null){
            query.setParameter("type", cond.getMoveType());
        }
        if(StringUtils.hasText(cond.getOperatorName())){
            query.setParameter("operatorName", cond.getOperatorName());
        }
        if(StringUtils.hasText(cond.getItemSearch())){
            query.setParameter("itemSearch", cond.getItemSearch());
        }
        if(cond.getLocationId() != null){
            query.setParameter("locationId", cond.getLocationId());
        }
        if(cond.getWarehouseId() != null){
            query.setParameter("warehouseId", cond.getWarehouseId());
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<StockHistory> resultList = query.getResultList();

        String countJpql = "select count(h) from StockHistory h"+
                " join h.user u join h.item i join h.location l join h.warehouse w where 1=1";

        if(cond.getMoveType() != null) countJpql += " and h.type = :type";
        if(StringUtils.hasText(cond.getOperatorName())) countJpql += " and u.name like concat('%', :operatorName, '%')";
        if(StringUtils.hasText(cond.getItemSearch())) countJpql += " and (i.name like concat('%', :itemSearch, '%') or i.barcode like concat('%', :itemSearch, '%'))";
        if(cond.getLocationId() != null) countJpql += " and l.id = :locationId";
        if(cond.getWarehouseId() != null) countJpql += " and w.id = :warehouseId";

        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

        if(cond.getMoveType() != null) countQuery.setParameter("type", cond.getMoveType());
        if(StringUtils.hasText(cond.getOperatorName())) countQuery.setParameter("operatorName", cond.getOperatorName());
        if(StringUtils.hasText(cond.getItemSearch())) countQuery.setParameter("itemSearch", cond.getItemSearch());
        if(cond.getLocationId() != null) countQuery.setParameter("locationId", cond.getLocationId());
        if(cond.getWarehouseId() != null) countQuery.setParameter("warehouseId", cond.getWarehouseId());

        Long totalCount = countQuery.getSingleResult();

        return new PageImpl<>(resultList,pageable,totalCount);
    }
}
