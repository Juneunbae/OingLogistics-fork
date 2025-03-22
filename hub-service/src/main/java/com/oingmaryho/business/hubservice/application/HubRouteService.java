package com.oingmaryho.business.hubservice.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.oingmaryho.business.hubservice.application.dto.request.HubPathRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteSearchResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.service.HubPathService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubRouteService {

	private final HubPathService hubPathService;

	public List<HubRouteSearchResponseServiceDto> getOptimalHubPath(HubPathRequestServiceDto requestDto) {
		hubPathService.getOptimalHubPath(requestDto.departureHubId(), requestDto.arriveHubId());
		return null;
	}
}
