package com.oingmaryho.business.hubservice.application;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.hubservice.application.dto.mapper.HubApplicationMapper;
import com.oingmaryho.business.hubservice.application.dto.request.HubSearchRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubsSearchRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubSearchResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.HubSearchCriteria;
import com.oingmaryho.business.hubservice.domain.repository.HubRepository;
import com.oingmaryho.business.hubservice.exception.ErrorCode;
import com.oingmaryho.business.hubservice.exception.HubException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubService {

	private final HubRepository hubRepository;
	private final HubApplicationMapper mapper;

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "hub", key = "#requestDto.id()")
	public HubSearchResponseServiceDto getHubById(HubSearchRequestServiceDto requestDto) {
		Hub hub = hubRepository.findActiveHubById(requestDto.id())
			.orElseThrow(() -> new HubException(ErrorCode.NOT_FOUND_HUB));

		return mapper.toHubSearchResponseServiceDto(hub);
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "hubs")
	public Page<HubSearchResponseServiceDto> searchHubs(HubsSearchRequestServiceDto requestDto, Pageable pageable) {
		Page<Hub> hubs = hubRepository.findDynamicQuery(createHubSearchCriteria(requestDto), pageable);

		return hubs.map(mapper::toHubSearchResponseServiceDto);
	}

	private HubSearchCriteria createHubSearchCriteria(HubsSearchRequestServiceDto requestDto) {
		return HubSearchCriteria.builder()
			.id(requestDto.id())
			.name(requestDto.name())
			.address(requestDto.address())
			.latitude(requestDto.latitude())
			.longitude(requestDto.longitude())
			.managerId(requestDto.managerId())
			.isDeleted(Boolean.FALSE)
			.build();
	}
}
