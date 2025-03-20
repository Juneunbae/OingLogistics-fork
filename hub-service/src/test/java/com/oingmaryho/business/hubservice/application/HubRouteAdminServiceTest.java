package com.oingmaryho.business.hubservice.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
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
import com.oingmaryho.business.hubservice.application.dto.request.HubRouteDeleteRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubRouteUpdateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteUpdateResponseServiceDto;
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

	@DisplayName("허브 이동 경로 ID와 허브 이동 경로 수정 정보를 통해 허브 이동 경로를 수정할 수 있다.")
	@Test
	void hub_route_update_test() {
		// Given
		UUID hubRouteId = UUID.randomUUID();
		UUID updateDepartureHubId = UUID.randomUUID();
		UUID updateArriveHubId = UUID.randomUUID();

		HubRoute existingHubRoute = HubRoute.builder()
			.id(hubRouteId)
			.departureHubId(UUID.randomUUID())
			.arriveHubId(UUID.randomUUID())
			.routeInfo(new RouteInfo(1, 1.0))
			.build();

		HubRouteUpdateRequestServiceDto requestDto = new HubRouteUpdateRequestServiceDto(
			updateDepartureHubId, updateArriveHubId, 2, 2.0
		);

		when(hubRouteRepository.findById(hubRouteId)).thenReturn(Optional.of(existingHubRoute));

		// When
		HubRouteUpdateResponseServiceDto result = hubRouteAdminService.updateHubRoute(hubRouteId, requestDto);

		// Then
		assertThat(result).isNotNull()
			.extracting(HubRouteUpdateResponseServiceDto::id)
			.isEqualTo(hubRouteId);
		assertThat(existingHubRoute)
			.extracting(
				HubRoute::getDepartureHubId,
				HubRoute::getArriveHubId,
				hr -> hr.getRouteInfo().getHubToHubTime(),
				hr -> hr.getRouteInfo().getDistance())
			.containsExactly(
				requestDto.departureHubId(),
				requestDto.arriveHubId(),
				requestDto.hubToHubTime(),
				requestDto.distance()
			);
	}

	@DisplayName("허브 이동 경로를 삭제하면 soft delete 처리된다.")
	@Test
	void hub_route_soft_delete_test() {
		// Given
		UUID hubRouteId = UUID.randomUUID();
		HubRoute hubRoute = HubRoute.builder()
			.id(hubRouteId)
			.departureHubId(UUID.randomUUID())
			.arriveHubId(UUID.randomUUID())
			.routeInfo(new RouteInfo(1, 1.0))
			.isDeleted(false)
			.build();

		HubRouteDeleteRequestServiceDto requestDto = new HubRouteDeleteRequestServiceDto(hubRouteId);

		when(hubRouteRepository.findById(hubRouteId)).thenReturn(Optional.of(hubRoute));

		// When
		hubRouteAdminService.deleteHubRoute(requestDto);

		// Then
		assertThat(hubRoute).isNotNull()
			.extracting(HubRoute::getIsDeleted)
			.isEqualTo(true);
	}

}