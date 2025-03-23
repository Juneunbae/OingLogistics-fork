package com.oingmaryho.business.hubservice.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.oingmaryho.business.hubservice.domain.HubRoute;
import com.oingmaryho.business.hubservice.domain.HubRouteSearchCriteria;
import com.oingmaryho.business.hubservice.domain.repository.HubRouteRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HubRouteRepositoryImpl implements HubRouteRepository {

	private final HubRouteJpaRepository hubRouteJpaRepository;
	private final HubRouteQueryRepository hubRouteQueryRepository;

	@Override
	public HubRoute save(HubRoute hubRoute) {
		return hubRouteJpaRepository.save(hubRoute);
	}

	@Override
	public Optional<HubRoute> findById(UUID id) {
		return hubRouteJpaRepository.findById(id);
	}

	@Override
	public Optional<HubRoute> findByIdAndIsDeletedFalse(UUID id) {
		return hubRouteJpaRepository.findByIdAndIsDeletedFalse(id);
	}

	@Override
	public Page<HubRoute> findDynamicQuery(HubRouteSearchCriteria criteria, Pageable pageable) {
		return hubRouteQueryRepository.findDynamicQuery(criteria, pageable);
	}

	@Override
	public Optional<HubRoute> findByDepartureHubIdAndArriveHubId(UUID departureHubId, UUID arriveHubId) {
		return hubRouteJpaRepository.findByDepartureHubIdAndArriveHubId(departureHubId, arriveHubId);
	}

	@Override
	public List<HubRoute> findAllByIsDeletedFalse() {
		return hubRouteJpaRepository.findAllByIsDeletedFalse();
	}

	@Override
	public List<HubRoute> findAllAssociatedWithHub(UUID hubId) {
		return hubRouteQueryRepository.findAllAssociatedWithHub(hubId);
	}
}
