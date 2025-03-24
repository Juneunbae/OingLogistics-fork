package com.oingmaryho.business.hubservice.infrastructure.repository.hub;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.repository.HubRepository;

public interface HubJpaRepository extends JpaRepository<Hub, UUID> {

	Optional<Hub> findAllByManagerIdAndIsDeletedFalse(Long managerId);
}
