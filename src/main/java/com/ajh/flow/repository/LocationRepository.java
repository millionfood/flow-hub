package com.ajh.flow.repository;

import com.ajh.flow.domain.Location;
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
    public List<Location> findAll(){
        return em.createQuery("select l from Location l",Location.class).getResultList();
    }
    public Optional<Location> findById(Long id) {
        return Optional.ofNullable(em.find(Location.class,id));
    }
    public boolean existsByLocCode(String locCode) {
        Long count = em.createQuery("select count(l) from Location l where l.locCode =:locCode", Long.class)
                .setParameter("locCode",locCode)
                .getSingleResult();
        return count > 0;
    }

    //-----------------수정/상태변경-----------------
    //변경 감지를 이용

}
