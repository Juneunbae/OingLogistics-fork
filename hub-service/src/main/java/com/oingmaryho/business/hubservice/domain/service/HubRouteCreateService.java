package com.oingmaryho.business.hubservice.domain.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.HubRoute;
import com.oingmaryho.business.hubservice.domain.RouteInfo;
import com.oingmaryho.business.hubservice.domain.repository.HubRepository;
import com.oingmaryho.business.hubservice.exception.ErrorCode;
import com.oingmaryho.business.hubservice.exception.HubException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubRouteCreateService {

	private final HubRepository hubRepository;
	private final GeoSpatialService geoSpatialService;

	public HubRoute createHubRoute(UUID departureHubId, UUID arriveHubId) {
		validateHubRoute(departureHubId, arriveHubId);

		Hub departureHub = findHubById(departureHubId);
		Hub arriveHub = findHubById(arriveHubId);
		RouteInfo routeInfo = geoSpatialService.getRouteInfo(departureHub.getAddress(), arriveHub.getAddress());

		return HubRoute.builder()
			.departureHubId(departureHubId)
			.arriveHubId(arriveHubId)
			.routeInfo(routeInfo)
			.build();
	}

	private void validateHubRoute(UUID departureHubId, UUID arriveHubId) {
		if(departureHubId.equals(arriveHubId)) {
			throw new HubException(ErrorCode.INVALID_HUB_ROUTE);
		}
	}

	private Hub findHubById(UUID hubId) {
		return hubRepository.findActiveHubById(hubId)
			.orElseThrow(() -> new HubException(ErrorCode.NOT_FOUND_HUB));
	}
}
