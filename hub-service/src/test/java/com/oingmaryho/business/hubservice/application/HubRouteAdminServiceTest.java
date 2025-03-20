package com.oingmaryho.business.hubservice.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oingmaryho.business.hubservice.application.dto.mapper.HubRouteApplicationMapper;
import com.oingmaryho.business.hubservice.application.dto.request.HubRouteCreateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.HubRoute;
import com.oingmaryho.business.hubservice.domain.RouteInfo;
import com.oingmaryho.business.hubservice.domain.repository.HubRouteRepository;
import com.oingmaryho.business.hubservice.domain.service.HubRouteCreateService;

@ExtendWith(MockitoExtension.class)
class HubRouteAdminServiceTest {

	@InjectMocks
	private HubRouteAdminService hubRouteAdminService;

	@Mock
	private HubRouteRepository hubRouteRepository;

	@Mock
	private HubRouteApplicationMapper mapper;

	@Mock
	private HubRouteCreateService hubRouteCreateService;

	@DisplayName("마스터 관리자는 허브 이동 경로를 생성할 수 있다.")
	@Test
	void hub_route_create_test() {
		// Given
		UUID departureHubId = UUID.randomUUID();
		UUID arriveHubId = UUID.randomUUID();

		HubRouteCreateRequestServiceDto requestDto = new HubRouteCreateRequestServiceDto(
			departureHubId, arriveHubId, 1, 1.0
		);

		HubRoute unsavedHubRoute = HubRoute.builder()
			.departureHubId(departureHubId)
			.arriveHubId(arriveHubId)
			.routeInfo(new RouteInfo(requestDto.hubToHubTime(), requestDto.distance()))
			.build();

		HubRoute savedHubRoute = HubRoute.builder()
			.id(UUID.randomUUID())
			.departureHubId(departureHubId)
			.arriveHubId(arriveHubId)
			.routeInfo(new RouteInfo(requestDto.hubToHubTime(), requestDto.distance()))
			.build();

		when(hubRouteCreateService.createHubRoute(
			requestDto.departureHubId(),
			requestDto.arriveHubId(),
			requestDto.hubToHubTime(),
			requestDto.distance()
		)).thenReturn(unsavedHubRoute);
		when(hubRouteRepository.save(unsavedHubRoute)).thenReturn(savedHubRoute);
		when(mapper.toHubRouteCreateResponseServiceDto(savedHubRoute))
			.thenReturn(new HubRouteCreateResponseServiceDto(savedHubRoute.getId()));

		// When
		HubRouteCreateResponseServiceDto result = hubRouteAdminService.createHubRoute(requestDto);

		// Then
		// 호출 검증
		verify(hubRouteCreateService, times(1)).createHubRoute(
			requestDto.departureHubId(),
			requestDto.arriveHubId(),
			requestDto.hubToHubTime(),
			requestDto.distance()
		);
		verify(hubRouteRepository, times(1)).save(unsavedHubRoute);
		verify(mapper, times(1)).toHubRouteCreateResponseServiceDto(savedHubRoute);

		// 응답값 검증
		assertThat(result).isNotNull()
			.extracting(HubRouteCreateResponseServiceDto::id)
			.isEqualTo(savedHubRoute.getId());
	}

}