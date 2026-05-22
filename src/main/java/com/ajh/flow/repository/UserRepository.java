package com.ajh.flow.repository;

import com.ajh.flow.domain.User;
import com.ajh.flow.dto.user.UserDetailDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;


    //-----------------등록-----------------
    public void save(User user) {
        em.persist(user);
    }

    //-----------------조회-----------------
    public List<UserDetailDto> findAll() {
        String jpql = "select new com.ajh.flow.dto.user.UserDetailDto("+
                "u.email,u.password,u.name,u.role)"+
                "from User u ";
        return em.createQuery(jpql, UserDetailDto.class).getResultList();
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
                "u.email,u.password,u.name,u.role)"+
                "from User u "+
                "where u.id = :id";
        return em.createQuery(jpql, UserDetailDto.class)
                .setParameter("id", id)
                .getResultList().stream().findFirst();
    }
    public Optional<UserDetailDto> findDetailDtoByEmail(String email) {
        String jpql = "select new com.ajh.flow.dto.user.UserDetailDto("+
                "u.email,u.password,u.name,u.role)"+
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
}
