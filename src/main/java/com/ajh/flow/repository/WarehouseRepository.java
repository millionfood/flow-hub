package com.ajh.flow.repository;

import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.domain.Warehouse;
import com.ajh.flow.dto.warehouse.WarehouseDetailDto;
import com.ajh.flow.dto.warehouse.WarehouseSearchCond;
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

import static com.ajh.flow.domain.QUser.user;
import static com.ajh.flow.domain.QWarehouse.warehouse;

@Repository
@RequiredArgsConstructor
public class WarehouseRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

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
    public List<Warehouse> findInboundAbleWarehouses(){
        return em.createQuery("select w from Warehouse w where w.useYn =:useYn",Warehouse.class)
                .setParameter("useYn", UseYn.Y).getResultList();
    }
    public Page<WarehouseDetailDto> findAllDetailWithPaging(Pageable pageable, WarehouseSearchCond cond) {
        List<WarehouseDetailDto> content = queryFactory
                .select(Projections.constructor(WarehouseDetailDto.class,
                        warehouse.id,
                        warehouse.name,
                        warehouse.address,
                        user.name,
                        user.tel,
                        warehouse.useYn
                        ))
                .from(warehouse)
                .join(warehouse.register,user)
                .where(
                        warehouseNameLike(cond.getWarehouseSearch()),
                        warehouseAdminNameLike(cond.getAdminName()),
                        warehouseAdminTelLike(cond.getAdminTel())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(warehouse.count())
                .from(warehouse)
                .where(
                    warehouseNameLike(cond.getWarehouseSearch()),
                    warehouseAdminNameLike(cond.getAdminName()),
                    warehouseAdminTelLike(cond.getAdminTel())
                )
                .fetchOne();
        long total =  totalCount != null ? totalCount : 0L;

        return new PageImpl<>(content, pageable, total);
    }

    //-----------------상태변경-----------------
    //변경 감지를 통한 수정


    //-----------------기타-----------------
    private BooleanExpression warehouseNameLike(String name){
        return StringUtils.hasText(name) ? warehouse.name.containsIgnoreCase(name.trim()) : null;
    }
    private BooleanExpression warehouseAdminNameLike(String name){
        return StringUtils.hasText(name) ? warehouse.register.name.like('%'+name+'%') : null;
    }
    private BooleanExpression warehouseAdminTelLike(String tel){
        return StringUtils.hasText(tel) ? warehouse.register.tel.like('%'+tel+'%') : null;
    }

}
