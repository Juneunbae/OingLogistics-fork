package com.oingmaryho.business.hubservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.oingmaryho.business.hubservice.domain.HubRoute;
import com.oingmaryho.business.hubservice.domain.criteria.HubRouteSearchCriteria;

public interface HubRouteRepository {

	HubRoute save(HubRoute hubRoute);

	Optional<HubRoute> findById(UUID id);

	Optional<HubRoute> findByIdAndIsDeletedFalse(UUID id);

	Page<HubRoute> findDynamicQuery(HubRouteSearchCriteria criteria, Pageable pageable);

	Optional<HubRoute> findByDepartureHubIdAndArriveHubId(UUID departureHubId, UUID arriveHubId);

	List<HubRoute> findAllByIsDeletedFalse();

	List<HubRoute> findAllAssociatedWithHub(UUID hubId);
}
