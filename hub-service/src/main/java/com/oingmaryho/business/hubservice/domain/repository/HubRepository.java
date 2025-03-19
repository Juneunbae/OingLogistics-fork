package com.oingmaryho.business.hubservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.oingmaryho.business.hubservice.domain.Hub;

public interface HubRepository {
	Hub save(Hub hub);

	Optional<Hub> findById(UUID id);
}
