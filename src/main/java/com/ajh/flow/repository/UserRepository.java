package com.ajh.flow.repository;

import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.common.constant.UserRole;
import com.ajh.flow.domain.User;
import com.ajh.flow.dto.user.UserDetailDto;
import com.ajh.flow.dto.user.UserSearchCond;
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

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;


    //-----------------등록-----------------
    public void save(User user) {
        em.persist(user);
    }

    //-----------------조회-----------------
    public List<UserDetailDto> findAll() {
        String jpql = "select new com.ajh.flow.dto.user.UserDetailDto("+
                "u.id,u.email,u.password,u.name,u.role,u.useYn)"+
                "from User u ";
        return em.createQuery(jpql, UserDetailDto.class).getResultList();
    }
    public List<UserDetailDto> findUsers(){
        String jpql = "select new com.ajh.flow.dto.user.UserDetailDto("+
                "u.id,u.email,u.password,u.name,u.role,u.useYn)"+
                "from User u where u.role != ADMIN";
        return em.createQuery(jpql, UserDetailDto.class).getResultList();
    }
    public Page<User> findAllWithPaging(Pageable pageable, UserSearchCond cond){
        List<User> content = queryFactory
                .selectFrom(user)
                .where(
                        user.role.ne(UserRole.ADMIN),
                        userNameLike(cond.getKeyword()),
                        userTypeEq(cond.getUseYn())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        user.role.ne(UserRole.ADMIN),
                        userNameLike(cond.getKeyword()),
                        userTypeEq(cond.getUseYn())
                )
                .fetchOne();
        long total = totalCount != null ? totalCount : 0L;

        return new PageImpl<>(content, pageable, total);
    }
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }
    public Optional<User> findByEmail(String email) {
        return em.createQuery("select u from User u where u.email =: email",User.class)
                .setParameter("email", email)
                .getResultList()
                .stream()
                .findFirst();
    }
    public Optional<UserDetailDto> findDetailDtoById(Long id) {
        String jpql = "select new com.ajh.flow.dto.user.UserDetailDto("+
                "u.id,u.email,u.password,u.name,u.role,u.useYn)"+
                "from User u "+
                "where u.id = :id";
        return em.createQuery(jpql, UserDetailDto.class)
                .setParameter("id", id)
                .getResultList().stream().findFirst();
    }
    public Optional<UserDetailDto> findDetailDtoByEmail(String email) {
        String jpql = "select new com.ajh.flow.dto.user.UserDetailDto("+
                "u.id,u.email,u.password,u.name,u.role,u.useYn)"+
                "from User u "+
                "where u.email = :email";
        return em.createQuery(jpql, UserDetailDto.class)
                .setParameter("email", email)
                .getResultList().stream().findFirst();
    }
    public boolean existSameEmail(String email){
        String jpql = "select count(u) from User u where u.email = :email";

        Long count = em.createQuery(jpql,Long.class)
                .setParameter("email", email).getSingleResult();

        return count > 0;
    }

    //-----------------수정-----------------

    //-----------------상태변경-----------------

    //-----------------기타-----------------
    //queryDsl 메서트
    private BooleanExpression userNameLike(String name){
        return StringUtils.hasText(name) ? user.name.like("%"+name+"%").or(user.email.like("%"+name+"%")) : null;
    }
    private BooleanExpression userTypeEq(UseYn useYn){
        return useYn != null ? user.useYn.eq(useYn) : null;
    }
}
