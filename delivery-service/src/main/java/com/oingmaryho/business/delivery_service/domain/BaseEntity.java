package com.oingmaryho.business.delivery_service.domain;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(updatable = false)
    private Long createdBy;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    @LastModifiedBy
    private Long updatedBy;

    @Column
    private LocalDateTime deletedAt;

    private Long deletedBy;

    private Boolean isDeleted = Boolean.FALSE;

    public void softDelete(Long deleteUserId) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deleteUserId;
        this.isDeleted = Boolean.TRUE;
    }
}