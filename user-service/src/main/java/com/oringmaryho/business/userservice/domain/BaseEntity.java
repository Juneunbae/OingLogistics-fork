package com.oringmaryho.business.userservice.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @CreatedDate
  @Column(updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime createdAt;

  @CreatedBy
  @Column(updatable = false)
  private Long createdBy;

  @LastModifiedDate
  @Column
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime updatedAt;

  @LastModifiedBy
  private Long updatedBy;

  @Column(updatable = false)
  private Long deletedBy;

  @Column(updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime deletedAt;

  @Column(name = "is_deleted", nullable = false)
  @Builder.Default
  private Boolean isDeleted = false;

  // soft delete 처리할 때 사용할 메소드
  public void delete(Long userId) {
    this.deletedBy = userId;
    this.deletedAt = LocalDateTime.now();
    this.isDeleted = true;
  }

  // soft delete 해제할 때 사용할 메소드
  public void restore() {
    this.deletedBy = null;
    this.deletedAt = null;
    this.isDeleted = false;
  }
}