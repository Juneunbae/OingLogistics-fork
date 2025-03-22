package com.oingmaryho.business.hubservice.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oingmaryho.business.hubservice.application.HubRouteService;
import com.oingmaryho.business.hubservice.application.HubService;
import com.oingmaryho.business.hubservice.application.dto.response.HubSearchResponseServiceDto;
import com.oingmaryho.business.hubservice.presentation.dto.mapper.HubPresentationMapper;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubPathRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubSearchRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubRouteSearchResponseDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubSearchResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hub-service")
@RequiredArgsConstructor
public class HubFeignClientController {

	private final HubService hubService;
	private final HubRouteService hubRouteService;
	private final HubPresentationMapper mapper;

	@GetMapping
	public ResponseEntity<HubSearchResponseDto> getHubById(@RequestParam(value = "managerId") Long managerId) {
		HubSearchRequestDto requestDto =
			new HubSearchRequestDto(null, null, null, null, null, managerId);
		HubSearchResponseServiceDto responseDto = hubService.getHubByManagerId(mapper.toHubsSearchRequestServiceDto(requestDto));
		return ResponseEntity.ok(mapper.toHubSearchResponseDto(responseDto));
	}

	@GetMapping("/path")
	public ResponseEntity<List<HubRouteSearchResponseDto>> getOptimalHubPath(
		@RequestParam(value = "departureHubId") UUID departureHubId,
		@RequestParam(value = "arriveAddress") String arriveAddress
	) {
		HubPathRequestDto requestDto = new HubPathRequestDto(departureHubId, arriveAddress);
		hubRouteService.getOptimalHubPath(mapper.toHubPathRequestServiceDto(requestDto));
		return null;
	}
}
