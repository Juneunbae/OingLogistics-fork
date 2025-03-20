package com.oingmaryho.business.hubservice.domain;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HubRouteSearchCriteria {
	private final UUID id;
	private final UUID departureHubId;
	private final UUID arriveHubId;
	private final Boolean isDeleted;
}
