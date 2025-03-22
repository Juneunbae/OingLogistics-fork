package com.oingmaryho.business.hubservice.domain.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.oingmaryho.business.hubservice.domain.HubRoute;

public interface ShortestPathService {

	List<HubRoute> getShortestPath(UUID departureHubId, UUID arriveHubId, List<HubRoute> allHubRoutes, Map<UUID, String> hubNameMatcher);
}
