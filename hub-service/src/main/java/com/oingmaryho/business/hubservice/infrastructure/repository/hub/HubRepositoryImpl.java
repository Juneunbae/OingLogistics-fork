package com.oingmaryho.business.hubservice.infrastructure.repository.hub;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.criteria.HubSearchCriteria;
import com.oingmaryho.business.hubservice.domain.repository.HubRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepository {

	private final HubJpaRepository hubJpaRepository;
	private final HubQueryRepository hubQueryRepository;

	@Override
	public Hub save(Hub hub) {
		return hubJpaRepository.save(hub);
	}

	@Override
	public Optional<Hub> findById(UUID id) {
		return hubJpaRepository.findById(id);
	}

	@Override
	public boolean existsById(UUID id) {
		return hubJpaRepository.existsById(id);
	}

	@Override
	public Page<Hub> findDynamicQuery(HubSearchCriteria criteria, Pageable pageable) {
		return hubQueryRepository.findDynamicQuery(criteria, pageable);
	}

	@Override
	public Optional<Hub> findActiveHubById(UUID id) {
		return hubQueryRepository.findActiveHubById(id);
	}

	@Override
	public List<Hub> findAllActiveHubs() {
		return hubQueryRepository.findAllActiveHubs();
	}

	@Override
	public Optional<Hub> findAllByManagerIdAndIsDeletedFalse(Long managerId) {
		return hubJpaRepository.findAllByManagerIdAndIsDeletedFalse(managerId);
	}
}
