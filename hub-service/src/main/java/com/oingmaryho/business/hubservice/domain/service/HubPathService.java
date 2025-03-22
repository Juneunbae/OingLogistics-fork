package com.oingmaryho.business.hubservice.domain.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.HubRoute;
import com.oingmaryho.business.hubservice.domain.repository.HubRepository;
import com.oingmaryho.business.hubservice.domain.repository.HubRouteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubPathService {

	private final HubRouteRepository hubRouteRepository;
	private final HubRepository hubRepository;
	private final ShortestPathService shortestPathService;

	public List<HubRoute> getOptimalHubPath(UUID departureHubId, UUID arriveHubId) {
		//모든 허브 경로 가져오기
		List<HubRoute> hubRoutes = hubRouteRepository.findAllByIsDeletedFalse();

		//허브 정보 가져오기(허브 간 관계 적용할 때 사용)
		List<Hub> hubs = hubRepository.findAllActiveHubs();

		//UUID와 허브 이름 매칭
		Map<UUID, String> hubNameMatcher = new HashMap<>();
		for(Hub hub : hubs) {
			hubNameMatcher.put(hub.getId(), hub.getName());
		}
		//최적 경로 구하기(정렬 순서 : 1. 거리 2. 거리가 같으면 소요 시간 순)
		List<HubRoute> shortestHubRoutes = shortestPathService.getShortestPath(departureHubId, arriveHubId, hubRoutes, hubNameMatcher);
		return shortestHubRoutes;
	}
}
