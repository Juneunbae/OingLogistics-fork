package com.oingmaryho.business.common.entity;

import jakarta.persistence.*;
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
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
	@Column(updatable = false)
	private Long deletedBy;

	@Column(updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime deletedAt;

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
}
