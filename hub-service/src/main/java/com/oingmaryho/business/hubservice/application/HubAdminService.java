package com.oingmaryho.business.hubservice.application;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.hubservice.application.dto.mapper.HubApplicationMapper;
import com.oingmaryho.business.hubservice.application.dto.request.HubCreateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubDeleteRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubSearchRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubUpdateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubsSearchAdminRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubSearchAdminResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubUpdateResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.Address;
import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.HubSearchCriteria;
import com.oingmaryho.business.hubservice.domain.repository.HubRepository;
import com.oingmaryho.business.hubservice.domain.service.HubCreateService;
import com.oingmaryho.business.hubservice.exception.ErrorCode;
import com.oingmaryho.business.hubservice.exception.HubException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubAdminService {

	private final HubCreateService hubCreateService;
	private final HubRepository hubRepository;
	private final HubApplicationMapper mapper;

	@Transactional
	public HubCreateResponseServiceDto createHub(HubCreateRequestServiceDto requestDto) {
		Hub hub = hubCreateService.createHub(
			requestDto.name(),
			requestDto.address(),
			requestDto.managerId()
		);

		Hub savedHub = hubRepository.save(hub);
		return mapper.toHubCreateResponseServiceDto(savedHub);
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "hub", key = "'admin:' + #requestDto.id()")
	public HubSearchAdminResponseServiceDto getHubById(HubSearchRequestServiceDto requestDto) {
		Hub hub = findHubById(requestDto.id());
		return mapper.toHubSearchAdminResponseServiceDto(hub);
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "hubs")
	public Page<HubSearchAdminResponseServiceDto> searchHubs(HubsSearchAdminRequestServiceDto requestDto, Pageable pageable) {
		Page<Hub> hubs = hubRepository.findDynamicQuery(createHubSearchAdminCriteria(requestDto), pageable);

		return hubs.map(mapper::toHubSearchAdminResponseServiceDto);
	}

	@Caching(evict = {
		@CacheEvict(cacheNames = "hub", key = "#id"),
		@CacheEvict(cacheNames = "hub", key = "'admin:' + #id"),
		@CacheEvict(cacheNames = "hubs", allEntries = true)
	})
	@Transactional
	public HubUpdateResponseServiceDto updateHub(UUID id, HubUpdateRequestServiceDto requestDto) {
		Hub hub = findHubById(id);
		Address newAddress = new Address(requestDto.address(), requestDto.latitude(), requestDto.longitude());

		hub.update(
			requestDto.name(),
			newAddress,
			requestDto.managerId()
		);
		return new HubUpdateResponseServiceDto(id);
	}

	@Transactional
	@Caching(evict = {
		@CacheEvict(cacheNames = "hub", key = "#requestDto.id()"),
		@CacheEvict(cacheNames = "hub", key = "'admin:' + #requestDto.id()"),
		@CacheEvict(cacheNames = "hubs", allEntries = true)
	})
	public void deleteHub(HubDeleteRequestServiceDto requestDto, Long userId) {
		Hub hub = findHubById(requestDto.id());
		hub.softDelete(userId);
	}

	private Hub findHubById(UUID id) {
		return hubRepository.findById(id)
			.orElseThrow(() -> new HubException(ErrorCode.NOT_FOUND_HUB));
	}

	private HubSearchCriteria createHubSearchAdminCriteria(HubsSearchAdminRequestServiceDto requestDto) {
		return HubSearchCriteria.builder()
			.id(requestDto.id())
			.name(requestDto.name())
			.address(requestDto.address())
			.latitude(requestDto.latitude())
			.longitude(requestDto.longitude())
			.managerId(requestDto.managerId())
			.isDeleted(requestDto.isDeleted())
			.build();
	}
}
