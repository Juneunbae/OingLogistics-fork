package com.oingmaryho.business.hubservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.oingmaryho.business.hubservice.domain.HubRoute;
import com.oingmaryho.business.hubservice.domain.HubRouteSearchCriteria;

public interface HubRouteRepository {

	HubRoute save(HubRoute hubRoute);

	Optional<HubRoute> findById(UUID id);

	Page<HubRoute> findDynamicQuery(HubRouteSearchCriteria criteria, Pageable pageable);
}
