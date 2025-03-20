package com.oingmaryho.business.hubservice.domain.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

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

	public HubRoute createHubRoute(UUID departureHubId, UUID arriveHubId, Integer hubToHubTime, Double distance) {
		validateHubRoute(departureHubId, arriveHubId);
		validateHub(departureHubId);
		validateHub(arriveHubId);

		RouteInfo routeInfo = new RouteInfo(hubToHubTime, distance);

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

	private void validateHub(UUID hubId) {
		if(!hubRepository.existsById(hubId)) {
			throw new HubException(ErrorCode.NOT_FOUND_HUB);
		}
	}
}
