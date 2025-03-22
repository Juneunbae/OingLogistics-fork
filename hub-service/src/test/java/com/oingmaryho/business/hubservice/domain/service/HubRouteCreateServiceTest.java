package com.oingmaryho.business.hubservice.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oingmaryho.business.hubservice.domain.repository.HubRepository;
import com.oingmaryho.business.hubservice.exception.ErrorCode;
import com.oingmaryho.business.hubservice.exception.HubException;

@ExtendWith(MockitoExtension.class)
class HubRouteCreateServiceTest {

	@InjectMocks
	private HubRouteCreateService hubRouteCreateService;

	@Mock
	private GeoSpatialService geoSpatialService;

	@Mock
	private HubRepository hubRepository;

	@DisplayName("출발지와 목적지가 같으면 허브 이동 경로를 생성할 수 없다.")
	@Test
	void create_hub_route_with_same_departure_and_arrival_hub_exception_test() {
		// Given
		UUID hubId = UUID.randomUUID();

		// When & Then
		assertThatThrownBy(() -> hubRouteCreateService
				.createHubRoute(hubId, hubId))
			.isInstanceOf(HubException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_HUB_ROUTE);

	}

	@DisplayName("존재하지 않는 허브 ID로 허브 이동 경로를 생성할 수 없다.")
	@Test
	void create_hub_route_with_non_existing_hub_exception_test() {
		// Given
		UUID nonExistHubId = UUID.randomUUID();
		UUID validHubId = UUID.randomUUID();

		// When & Then
		assertThatThrownBy(() -> hubRouteCreateService
				.createHubRoute(nonExistHubId, validHubId))
			.isInstanceOf(HubException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_HUB);

		assertThatThrownBy(() -> hubRouteCreateService
			.createHubRoute(validHubId, nonExistHubId))
			.isInstanceOf(HubException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_HUB);
	}
}