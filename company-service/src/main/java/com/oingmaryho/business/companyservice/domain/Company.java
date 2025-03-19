package com.oingmaryho.business.companyservice.domain;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.oingmaryho.business.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Table(name = "p_company")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String type;

	@Column(nullable = false)
	private Long managerId;

	@Column(nullable = false)
	private UUID manageHubId;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private Boolean isDeleted;

	public void update(String name, String type, UUID manageHubId, String address) {
		Optional.ofNullable(name).filter(n -> !n.isBlank()).ifPresent(value -> this.name = value);
		Optional.ofNullable(type).filter(t -> !t.isBlank()).ifPresent(value -> this.type = value);
		Optional.ofNullable(manageHubId).ifPresent(value -> this.manageHubId = value);
		Optional.ofNullable(address).filter(a -> !a.isBlank()).ifPresent(value -> this.address = value);
	}

}
