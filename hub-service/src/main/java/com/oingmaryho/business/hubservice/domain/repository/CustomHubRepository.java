package com.oingmaryho.business.hubservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.HubSearchCriteria;

public interface CustomHubRepository {
	Page<Hub> findDynamicQuery(HubSearchCriteria criteria, Pageable pageable);

	Optional<Hub> findActiveHubById(UUID id);
}
