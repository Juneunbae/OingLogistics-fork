package com.oingmaryho.business.hubservice.presentation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oingmaryho.business.hubservice.application.HubRouteService;
import com.oingmaryho.business.hubservice.presentation.dto.mapper.HubPresentationMapper;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubPathRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubRouteSearchResponseDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubSearchResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hub-service")
@RequiredArgsConstructor
public class HubFeignClientController {

	private final HubRouteService hubRouteService;
	private final HubPresentationMapper mapper;

	@GetMapping("/{id}")
	public ResponseEntity<HubSearchResponseDto> getHubById(Long id) {
		return null;
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
