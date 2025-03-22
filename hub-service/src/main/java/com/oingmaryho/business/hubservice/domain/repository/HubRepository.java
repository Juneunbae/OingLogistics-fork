package com.oingmaryho.business.hubservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.HubSearchCriteria;

public interface HubRepository {
	Hub save(Hub hub);

	Optional<Hub> findById(UUID id);

	boolean existsById(UUID id);

	Page<Hub> findDynamicQuery(HubSearchCriteria criteria, Pageable pageable);

	Optional<Hub> findActiveHubById(UUID id);

	List<Hub> findAllActiveHubs();

	Optional<Hub> findAllByManagerIdAndIsDeletedFalse(Long managerId);
}
