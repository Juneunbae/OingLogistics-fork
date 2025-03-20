package com.oingmaryho.business.hubservice.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.hubservice.application.dto.mapper.HubRouteApplicationMapper;
import com.oingmaryho.business.hubservice.application.dto.request.HubRouteCreateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.HubRoute;
import com.oingmaryho.business.hubservice.domain.repository.HubRouteRepository;
import com.oingmaryho.business.hubservice.domain.service.HubRouteCreateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubRouteAdminService {

	private final HubRouteRepository hubRouteRepository;
	private final HubRouteApplicationMapper mapper;
	private final HubRouteCreateService hubRouteCreateService;

	@Transactional
	public HubRouteCreateResponseServiceDto createHubRoute(HubRouteCreateRequestServiceDto requestDto) {
		HubRoute hubRoute = hubRouteCreateService.createHubRoute(
			requestDto.departureHubId(),
			requestDto.arriveHubId(),
			requestDto.hubToHubTime(),
			requestDto.distance()
		);

		HubRoute savedHubRoute = hubRouteRepository.save(hubRoute);
		return mapper.toHubRouteCreateResponseServiceDto(savedHubRoute);
	}
}
