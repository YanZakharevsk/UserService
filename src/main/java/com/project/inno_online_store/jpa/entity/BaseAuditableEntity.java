package com.project.inno_online_store.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditableEntity {


    @CreatedDate
    @Column(updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private LocalDateTime createdAt;

    @LastModifiedBy
    @EqualsAndHashCode.Include
    @ToString.Include
    private LocalDateTime updatedAt;
}
