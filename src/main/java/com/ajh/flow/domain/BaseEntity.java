package com.ajh.flow.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass //상속을 위한 어노테이션
@EntityListeners(AuditingEntityListener.class) // @CreatedDate,@LastModifiedDate를 사용하기 위함
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false) //생성일은 수정하지 않는다.
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
