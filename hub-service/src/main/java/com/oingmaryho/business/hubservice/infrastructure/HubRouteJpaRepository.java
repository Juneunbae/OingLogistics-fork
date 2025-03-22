package com.oingmaryho.business.hubservice.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oingmaryho.business.hubservice.domain.HubRoute;

public interface HubRouteJpaRepository extends JpaRepository<HubRoute, UUID> {

	Optional<HubRoute> findByDepartureHubIdAndArriveHubId(UUID departureHubId, UUID arriveHubId);

	List<HubRoute> findAllByIsDeletedFalse();
}
