package com.oingmaryho.business.hubservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.oingmaryho.business.hubservice.domain.HubRoute;

public interface HubRouteRepository {

	HubRoute save(HubRoute hubRoute);

	Optional<HubRoute> findById(UUID id);
}
