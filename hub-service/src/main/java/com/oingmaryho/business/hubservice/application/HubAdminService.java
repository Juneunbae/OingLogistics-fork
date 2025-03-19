package com.oingmaryho.business.hubservice.application;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.hubservice.application.dto.mapper.HubApplicationMapper;
import com.oingmaryho.business.hubservice.application.dto.request.HubCreateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubDeleteRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubUpdateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubUpdateResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.Address;
import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.repository.HubRepository;
import com.oingmaryho.business.hubservice.exception.ErrorCode;
import com.oingmaryho.business.hubservice.exception.HubException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubAdminService {

	private final HubRepository hubRepository;
	private final HubApplicationMapper mapper;

	// TODO : Auditing 추가하기
	@Transactional
	public HubCreateResponseServiceDto createHub(HubCreateRequestServiceDto requestDto) {
		Hub hub = mapper.toHub(requestDto);

		Hub savedHub = hubRepository.save(hub);
		return mapper.toHubCreateResponseServiceDto(savedHub);
	}

	// TODO : Auditing 추가하기
	// TODO : 전체 조회 캐시 키 확인하기
	@Caching(evict = {
		@CacheEvict(cacheNames = "hub", key = "#id"),
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

	// TODO : Auditing 추가하기
	@Transactional
	public void deleteHub(HubDeleteRequestServiceDto requestDto) {
		Hub hub = findHubById(requestDto.id());

		hub.delete();
	}

	private Hub findHubById(UUID id) {
		return hubRepository.findById(id)
			.orElseThrow(() -> new HubException(ErrorCode.NOT_FOUND));
	}
}
