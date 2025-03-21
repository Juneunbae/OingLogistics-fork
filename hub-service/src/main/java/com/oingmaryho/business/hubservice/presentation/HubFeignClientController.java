package com.oingmaryho.business.hubservice.presentation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oingmaryho.business.hubservice.application.HubRouteService;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubPathRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubRouteSearchResponseDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubSearchResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hub-service")
@RequiredArgsConstructor
public class HubFeignClientController {

	private final HubRouteService hubRouteService;

	@GetMapping("/{id}")
	public ResponseEntity<HubSearchResponseDto> getHubById(Long id) {
		return null;
	}

	@GetMapping("/path")
	public ResponseEntity<List<HubRouteSearchResponseDto>> getOptimalHubPath(
		@RequestBody HubPathRequestDto requestDto
	) {
		return null;
	}
}
