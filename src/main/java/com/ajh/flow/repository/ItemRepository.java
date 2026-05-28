package com.ajh.flow.repository;

import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.common.exception.EntityNotFoundException;
import com.ajh.flow.domain.Item;
import com.ajh.flow.dto.item.ItemDetailDto;
import com.ajh.flow.dto.item.ItemSearchCond;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static com.ajh.flow.domain.QItem.item;
import static com.ajh.flow.domain.QStock.stock;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ItemRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    //-----------------저장-----------------
    //저장
    public void save(Item item) {
        em.persist(item);
    }


    //-----------------조회-----------------
    //전체조회
    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class).getResultList();

    }
    public Page<ItemDetailDto> findAllItemDetailDto(ItemSearchCond cond, Pageable pageable) {
//        String jpql = "select new com.ajh.flow.dto.item.ItemDetailDto("+
//                " i.id,i.name,i.barcode,i.description,i.price,i.unit,i.createdDate,"+
//                " (select coalesce(sum(s.quantity),0) from Stock s where s.item = i))"+
//                " from Item i"+
//                " order by i.id desc";
//        return em.createQuery(jpql, ItemDetailDto.class).getResultList();
            List<ItemDetailDto> content = queryFactory
                    .select(Projections.constructor(ItemDetailDto.class,
                        item.id,
                        item.name,
                        item.barcode,
                        item.description,
                        item.price,
                        item.unit,
                        item.createdDate,
                        stock.quantity.sum().coalesce(0L)
                    ))
                    .from(item)
                    .leftJoin(stock).on(stock.item.eq(item))
                    .where(
                            itemNameLike(cond.getItemKeyword())
                    )
                    .groupBy(item.id)
                    .orderBy(item.id.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            Long totalCount = queryFactory
                    .select(item.count())
                    .from(item)
                    .where(
                        itemNameLike(cond.getItemKeyword())
                    )
                    .fetchOne();

            long total = totalCount != null ? totalCount : 0L;

            return new PageImpl<>(content, pageable, total);
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



    //-----------------수정-----------------
    //상품 정보 수정 - 변경감지를 이용해 처리(service계층에서)


    //-----------------삭제-----------------
    //상품 삭제
    public void deleteById(Long id) {
        Item item = em.find(Item.class, id);
        if(item == null){
            throw new EntityNotFoundException("삭제하려는 상품을 찾을 수 없습니다.");
        }
        em.remove(item);
    }

    //-----------------기타-----------------
    //QueryDsl 전용 메서드
    private BooleanExpression itemNameLike(String itemName){
        return StringUtils.hasText(itemName) ? item.name.like("%"+itemName+"%").or(item.barcode.like("%"+itemName+"%")) : null;
    }
    private BooleanExpression statusEq(StockStatus status){
        return status != null ? stock.status.eq(status) : null;
    }
}
