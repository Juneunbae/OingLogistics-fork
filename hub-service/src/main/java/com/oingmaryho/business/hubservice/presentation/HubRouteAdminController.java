package com.oingmaryho.business.hubservice.presentation;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oingmaryho.business.hubservice.application.HubRouteAdminService;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteUpdateResponseServiceDto;
import com.oingmaryho.business.hubservice.presentation.dto.mapper.HubRoutePresentationMapper;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubRouteCreateRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubRouteUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/v1/hub-routes")
@RequiredArgsConstructor
public class HubRouteAdminController {

	private final HubRouteAdminService hubRouteAdminService;
	private final HubRoutePresentationMapper mapper;

	@PostMapping
	public ResponseEntity<?> createHubRoute(@RequestBody HubRouteCreateRequestDto requestDto) {
		HubRouteCreateResponseServiceDto responseDto = hubRouteAdminService
			.createHubRoute(mapper.toHubRouteCreateRequestServiceDto(requestDto));

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(mapper.toHubRouteCreateResponseDto(responseDto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateHubRoute(
		@PathVariable UUID id,
		@RequestBody HubRouteUpdateRequestDto requestDto
	) {
		HubRouteUpdateResponseServiceDto responseDto = hubRouteAdminService
			.updateHubRoute(id, mapper.toHubRouteUpdateRequestServiceDto(requestDto));

		return ResponseEntity.ok(mapper.toHubRouteUpdateResponseDto(responseDto));
	}
}
