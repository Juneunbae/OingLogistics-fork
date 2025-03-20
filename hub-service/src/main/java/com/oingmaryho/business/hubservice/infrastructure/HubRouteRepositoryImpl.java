package com.oingmaryho.business.hubservice.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.oingmaryho.business.hubservice.domain.HubRoute;
import com.oingmaryho.business.hubservice.domain.repository.HubRouteRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HubRouteRepositoryImpl implements HubRouteRepository {

	private final HubRouteJpaRepository hubRouteJpaRepository;

	@Override
	public HubRoute save(HubRoute hubRoute) {
		return hubRouteJpaRepository.save(hubRoute);
	}

	@Override
	public Optional<HubRoute> findById(UUID id) {
		return hubRouteJpaRepository.findById(id);
	}
}
