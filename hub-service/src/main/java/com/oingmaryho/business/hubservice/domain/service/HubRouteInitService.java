package com.oingmaryho.business.hubservice.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.HubRoute;
import com.oingmaryho.business.hubservice.domain.RouteInfo;
import com.oingmaryho.business.hubservice.domain.repository.HubRepository;
import com.oingmaryho.business.hubservice.domain.repository.HubRouteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubRouteInitService {

	private final HubRepository hubRepository;
	private final HubRouteRepository hubRouteRepository;
	private final HubRouteCreateService hubRouteCreateService;
	private final GeoSpatialService geoSpatialService;

	public void initHubRoute(Hub hub) {
		// 존재하는 허브 리스트 조회
		List<Hub> hubs = hubRepository.findAllActiveHubs();

		hubs.stream()
			.filter(h -> !h.getId().equals(hub.getId()))
			.forEach(other -> {
				RouteInfo toRouteInfo = geoSpatialService.getRouteInfo(hub.getAddress(), other.getAddress());
				HubRoute hubRoute1 = hubRouteCreateService.createHubRoute(
					hub.getId(),
					other.getId(),
					toRouteInfo.getHubToHubTime(),
					toRouteInfo.getDistance()
				);
				hubRouteRepository.save(hubRoute1);

				RouteInfo fromRouteInfo = geoSpatialService.getRouteInfo(other.getAddress(), hub.getAddress());
				HubRoute hubRoute2 = hubRouteCreateService.createHubRoute(
					other.getId(),
					hub.getId(),
					fromRouteInfo.getHubToHubTime(),
					fromRouteInfo.getDistance()
				);
				hubRouteRepository.save(hubRoute2);
			});
	}
}
