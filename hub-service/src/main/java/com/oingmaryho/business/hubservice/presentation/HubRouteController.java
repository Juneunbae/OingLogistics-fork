package com.oingmaryho.business.hubservice.presentation;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oingmaryho.business.hubservice.application.HubRouteService;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteSearchResponseServiceDto;
import com.oingmaryho.business.hubservice.presentation.dto.mapper.HubRoutePresentationMapper;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubRouteSearchRequestDto;
import com.oingmaryho.business.hubservice.utils.PageableUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/hub-routes")
@RequiredArgsConstructor
public class HubRouteController {

	private final HubRouteService hubRouteService;
	private final HubRoutePresentationMapper mapper;

	@GetMapping
	public ResponseEntity<?> searchHubRoutes(
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
		@RequestParam(value = "by", required = false) String by,
		@RequestParam(value = "id", required = false) UUID id,
		@RequestParam(value = "departureHubId", required = false) UUID departureHubId,
		@RequestParam(value = "arriveHubId", required = false) UUID arriveHubId
	) {
		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);
		HubRouteSearchRequestDto requestDto = new HubRouteSearchRequestDto(id, departureHubId, arriveHubId);

		Page<HubRouteSearchResponseServiceDto> responseDto = hubRouteService
			.searchHubRoutes(mapper.toHubRoutesSearchRequestServiceDto(requestDto), pageable);

		return ResponseEntity.ok(responseDto.map(mapper::toHubRouteSearchResponseDto));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getHubRouteById(@PathVariable UUID id) {
		HubRouteSearchResponseServiceDto response = hubRouteService.getHubRouteById(mapper.toHubRouteSearchRequestServiceDto(id));
		return ResponseEntity.ok(mapper.toHubRouteSearchResponseDto(response));
	}
}
