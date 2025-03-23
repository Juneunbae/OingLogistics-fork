package com.oingmaryho.business.hubservice.domain.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.oingmaryho.business.hubservice.domain.HubRoute;
import com.oingmaryho.business.hubservice.domain.repository.HubRouteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubRouteDeleteService {

	private final HubRouteRepository hubRouteRepository;

	public void deleteHubRouteAssociatedWithHub(UUID hubId, Long userId) {
		List<HubRoute> associatedHubRoutes = hubRouteRepository.findAllAssociatedWithHub(hubId);

		associatedHubRoutes.forEach(
			hubRoute -> hubRoute.softDelete(userId)
		);
	}
}
