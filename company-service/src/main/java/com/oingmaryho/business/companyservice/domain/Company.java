package com.oingmaryho.business.companyservice.domain;

import java.util.UUID;

import com.oingmaryho.business.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
}
