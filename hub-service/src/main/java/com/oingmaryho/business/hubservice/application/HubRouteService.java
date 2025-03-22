package com.oingmaryho.business.hubservice.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.oingmaryho.business.hubservice.application.dto.request.HubPathRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteSearchResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.service.HubInfoService;
import com.oingmaryho.business.hubservice.domain.service.HubPathService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubRouteService {

	private final HubPathService hubPathService;
	private final HubInfoService hubInfoService;

	public List<HubRouteSearchResponseServiceDto> getOptimalHubPath(HubPathRequestServiceDto requestDto) {
		Hub nearestHub = hubInfoService.getNearestHubFromAddress(requestDto.arriveAddress());
		hubPathService.getOptimalHubPath(requestDto.departureHubId(), nearestHub.getId());
		return null;
	}
}
