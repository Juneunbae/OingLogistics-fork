package com.oingmaryho.business.hubservice.presentation;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oingmaryho.business.hubservice.application.HubService;
import com.oingmaryho.business.hubservice.application.dto.response.HubSearchResponseServiceDto;
import com.oingmaryho.business.hubservice.presentation.dto.mapper.HubPresentationMapper;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubSearchRequestDto;
import com.oingmaryho.business.hubservice.utils.PageableUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/hubs")
@RequiredArgsConstructor
public class HubController {

	private final HubService hubService;
	private final HubPresentationMapper mapper;

	@GetMapping
	public ResponseEntity<?> searchHubs(
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
		@RequestParam(value = "by", required = false) String by,
		@RequestParam(value = "id", required = false) UUID id,
		@RequestParam(value = "name", required = false) String name,
		@RequestParam(value = "address", required = false) String address,
		@RequestParam(value = "latitude", required = false) Double latitude,
		@RequestParam(value = "longitude", required = false) Double longitude,
		@RequestParam(value = "managerId", required = false) Long managerId
	) {
		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);
		HubSearchRequestDto requestDto = new HubSearchRequestDto(id, name, address, latitude, longitude, managerId);

		Page<HubSearchResponseServiceDto> responseDto = hubService.searchHubs(mapper.toHubsSearchRequestServiceDto(requestDto), pageable);
		return ResponseEntity.ok(responseDto.map(mapper::toHubSearchResponseDto));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getHubById(@PathVariable UUID id) {
		HubSearchResponseServiceDto responseDto = hubService.getHubById(mapper.toHubSearchRequestServiceDto(id));
		return ResponseEntity.ok(mapper.toHubSearchResponseDto(responseDto));
	}
}
