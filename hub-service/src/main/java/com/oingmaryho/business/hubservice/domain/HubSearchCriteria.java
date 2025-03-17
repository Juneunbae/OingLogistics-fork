package com.oingmaryho.business.hubservice.domain;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HubSearchCriteria {
	private final UUID id;
	private final String name;
	private final String address;
	private final Double latitude;
	private final Double longitude;
	private final Long managerId;
	private final Boolean isDeleted;
}
