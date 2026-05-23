package com.ajh.flow.repository;

import com.ajh.flow.domain.StockHistory;
import com.ajh.flow.domain.UserHistory;
import com.ajh.flow.dto.history.HistorySearchCond;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HistoryRepository {

    private final EntityManager em;

    //-----------------User-----------------
    public void saveUserHistory(UserHistory userHistory) {
        em.persist(userHistory);
    }

    public List<UserHistory> findAllUserHistory(HistorySearchCond cond) {
        String jpql = "select h from UserHistory h "+
                "join fetch h.admin a " +
                "join fetch h.targetUser t "+
                "where 1=1";
        //조건에 다른 동적 JPQL 문자열 조립
        if(cond.getType() != null){
            jpql += " and h.type = :type";
        }
        if(StringUtils.hasText(cond.getAdminSearch())){
            jpql += " and (a.name like concat('%', :adminSearch, '%') or a.email like concat('%', :adminSearch, '%'))";
        }
        if(StringUtils.hasText(cond.getTargetSearch())){
            jpql += " and (t.name like concat('%', :targetSearch, '%')) or t.email like concat('%', :targetSearch, '%')";
        }
        if(StringUtils.hasText(cond.getRemarkKeyword())){
            jpql += " and h.remark like concat('%', :remarkKeyword, '%')";
        }
        //최신 로그 순 정렬
        jpql += " order by h.id desc";

        TypedQuery<UserHistory> query = em.createQuery(jpql, UserHistory.class);

        //조립된 쿼리에 파라미터 바인딩
        if(cond.getType() != null){
            query.setParameter("type", cond.getType());
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

        return query.getResultList();
    }


    //-----------------Item-----------------
    public void saveStockHistory(StockHistory stockHistory) {
        em.persist(stockHistory);
    }
}
