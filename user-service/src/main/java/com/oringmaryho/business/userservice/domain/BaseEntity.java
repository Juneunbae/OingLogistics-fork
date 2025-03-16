package com.oringmaryho.business.userservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @Id
  @Column(updatable = false, nullable = false)
  private Long id;

  @Column(updatable = false)
  private Long deletedBy;

  @Column(updatable = false)
  private LocalDateTime deletedAt;

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

  @Column(name = "is_deleted", nullable = false)
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
