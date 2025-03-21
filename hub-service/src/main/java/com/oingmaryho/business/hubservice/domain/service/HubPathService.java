package com.oingmaryho.business.hubservice.domain.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.oingmaryho.business.hubservice.domain.HubRoute;
import com.oingmaryho.business.hubservice.domain.repository.HubRouteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubPathService {

	private final HubRouteRepository hubRouteRepository;
	private final ShortestPathService shortestPathService;

	public List<HubRoute> getOptimalHubPath(UUID departureHubId, UUID arriveHubId) {
		return null;
	}
}
