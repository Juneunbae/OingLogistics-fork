package com.oingmaryho.business.hubservice.presentation.dto.request;

import java.util.UUID;

public record HubPathRequestDto(
	UUID departureHubId,
	String arriveAddress
) {
}
