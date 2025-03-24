package com.oingmaryho.business.hubservice.application.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.hubservice.application.dto.mapper.HubRouteApplicationMapper;
import com.oingmaryho.business.hubservice.application.dto.request.HubPathRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubRouteSearchRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubRoutesSearchRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteSearchResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.HubRoute;
import com.oingmaryho.business.hubservice.domain.criteria.HubRouteSearchCriteria;
import com.oingmaryho.business.hubservice.domain.repository.HubRouteRepository;
import com.oingmaryho.business.hubservice.domain.service.HubInfoService;
import com.oingmaryho.business.hubservice.domain.service.HubPathService;
import com.oingmaryho.business.hubservice.exception.ErrorCode;
import com.oingmaryho.business.hubservice.exception.HubException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubRouteService {

	private final HubPathService hubPathService;
	private final HubInfoService hubInfoService;
	private final HubRouteApplicationMapper mapper;
	private final HubRouteRepository hubRouteRepository;

	@Transactional(readOnly = true)
	@Cacheable(value = "hubRoute", key = "#requestDto.id()")
	public HubRouteSearchResponseServiceDto getHubRouteById(HubRouteSearchRequestServiceDto requestDto) {
		HubRoute hubRoute = hubRouteRepository.findByIdAndIsDeletedFalse(requestDto.id())
			.orElseThrow(() -> new HubException(ErrorCode.NOT_FOUND_HUB_ROUTE));
		return mapper.toHubRouteSearchResponseServiceDto(hubRoute);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "hubRoutes")
	public Page<HubRouteSearchResponseServiceDto> searchHubRoutes(HubRoutesSearchRequestServiceDto requestDto, Pageable pageable) {
		HubRouteSearchCriteria criteria = createHubRouteSearchCriteria(requestDto);
		Page<HubRoute> response = hubRouteRepository.findDynamicQuery(criteria, pageable);
		return response.map(mapper::toHubRouteSearchResponseServiceDto);
	}

	@Transactional
	@Cacheable(value = "hubPath")
	public List<HubRouteSearchResponseServiceDto> getOptimalHubPath(HubPathRequestServiceDto requestDto) {
		Hub nearestHub = hubInfoService.getNearestHubFromAddress(requestDto.arriveAddress());
		List<HubRoute> response = hubPathService.getOptimalHubPath(requestDto.departureHubId(), nearestHub.getId());
		return response.stream()
			.map(mapper::toHubRouteSearchResponseServiceDto)
			.toList();
	}

	private HubRouteSearchCriteria createHubRouteSearchCriteria(HubRoutesSearchRequestServiceDto requestDto) {
		return HubRouteSearchCriteria.builder()
			.id(requestDto.id())
			.departureHubId(requestDto.departureHubId())
			.arriveHubId(requestDto.arriveHubId())
			.build();
	}
}
