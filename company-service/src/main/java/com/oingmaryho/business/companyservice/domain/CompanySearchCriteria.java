package com.oingmaryho.business.companyservice.domain;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanySearchCriteria {
	private final UUID id;
	private final String name;
	private final CompanyType type;
	private final Long managerId;
	private final UUID manageHubId;
	private final String address;
	private final Boolean isDeleted;
}
