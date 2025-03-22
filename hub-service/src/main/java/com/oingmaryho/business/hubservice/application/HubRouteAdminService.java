package com.oingmaryho.business.hubservice.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oingmaryho.business.hubservice.application.dto.mapper.HubRouteApplicationMapper;
import com.oingmaryho.business.hubservice.application.dto.request.HubRouteCreateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubRouteDeleteRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubRouteSearchAdminRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubRouteUpdateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubRoutesSearchAdminRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteSearchAdminResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteUpdateResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.HubRoute;
import com.oingmaryho.business.hubservice.domain.HubRouteSearchCriteria;
import com.oingmaryho.business.hubservice.domain.RouteInfo;
import com.oingmaryho.business.hubservice.domain.repository.HubRouteRepository;
import com.oingmaryho.business.hubservice.domain.service.HubRouteCreateService;
import com.oingmaryho.business.hubservice.exception.ErrorCode;
import com.oingmaryho.business.hubservice.exception.HubException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubRouteAdminService {

	private final HubRouteRepository hubRouteRepository;
	private final HubRouteApplicationMapper mapper;
	private final HubRouteCreateService hubRouteCreateService;

	@Transactional
	public HubRouteCreateResponseServiceDto createHubRoute(HubRouteCreateRequestServiceDto requestDto) {
		HubRoute hubRoute = hubRouteCreateService.createHubRoute(requestDto.departureHubId(), requestDto.arriveHubId());

		HubRoute savedHubRoute = hubRouteRepository.save(hubRoute);
		return mapper.toHubRouteCreateResponseServiceDto(savedHubRoute);
	}

	@Transactional(readOnly = true)
	public HubRouteSearchAdminResponseServiceDto getHubRouteById(HubRouteSearchAdminRequestServiceDto requestDto) {
		HubRoute hubRoute = findHubRouteById(requestDto.id());
		return mapper.toHubRouteSearchAdminResponseServiceDto(hubRoute);
	}

	@Transactional(readOnly = true)
	public Page<HubRouteSearchAdminResponseServiceDto> searchHubRoutes(HubRoutesSearchAdminRequestServiceDto requestDto, Pageable pageable) {
		Page<HubRoute> hubRoutes = hubRouteRepository.findDynamicQuery(createHubRouteSearchCriteria(requestDto), pageable);

		return hubRoutes.map(mapper::toHubRouteSearchAdminResponseServiceDto);
	}

	// TODO : Auditing 설정 추가
	@Transactional
	public HubRouteUpdateResponseServiceDto updateHubRoute(UUID id, HubRouteUpdateRequestServiceDto requestDto) {
		HubRoute hubRoute = findHubRouteById(id);

		RouteInfo newRouteInfo = new RouteInfo(requestDto.hubToHubTime(), requestDto.distance());
		hubRoute.update(
			requestDto.departureHubId(),
			requestDto.arriveHubId(),
			newRouteInfo
		);
		return new HubRouteUpdateResponseServiceDto(id);
	}

	// TODO : Auditing 설정 추가
	@Transactional
	public void deleteHubRoute(HubRouteDeleteRequestServiceDto requestDto) {
		HubRoute hubRoute = findHubRouteById(requestDto.id());
		hubRoute.delete();
	}

	private HubRoute findHubRouteById(UUID id) {
		return hubRouteRepository.findById(id)
			.orElseThrow(() -> new HubException(ErrorCode.NOT_FOUND_HUB_ROUTE));
	}

	private HubRouteSearchCriteria createHubRouteSearchCriteria(HubRoutesSearchAdminRequestServiceDto requestDto) {
		return HubRouteSearchCriteria.builder()
			.id(requestDto.id())
			.departureHubId(requestDto.departureHubId())
			.arriveHubId(requestDto.arriveHubId())
			.isDeleted(requestDto.isDeleted())
			.build();
	}
}
