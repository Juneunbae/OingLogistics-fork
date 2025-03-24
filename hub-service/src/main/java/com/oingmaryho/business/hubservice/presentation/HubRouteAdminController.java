package com.oingmaryho.business.hubservice.presentation;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oingmaryho.business.common.domain.type.UserRoleType;
import com.oingmaryho.business.common.infrastructure.annotation.RequiredRoles;
import com.oingmaryho.business.hubservice.application.service.HubRouteAdminService;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteSearchAdminResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteUpdateResponseServiceDto;
import com.oingmaryho.business.hubservice.presentation.dto.mapper.HubRoutePresentationMapper;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubRouteCreateRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubRouteSearchAdminRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubRouteUpdateRequestDto;
import com.oingmaryho.business.hubservice.utils.PageableUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/v1/hub-routes")
@RequiredArgsConstructor
public class HubRouteAdminController {

	private final HubRouteAdminService hubRouteAdminService;
	private final HubRoutePresentationMapper mapper;

	@RequiredRoles(UserRoleType.MASTER)
	@PostMapping
	public ResponseEntity<?> createHubRoute(@RequestBody HubRouteCreateRequestDto requestDto) {
		HubRouteCreateResponseServiceDto responseDto = hubRouteAdminService
			.createHubRoute(mapper.toHubRouteCreateRequestServiceDto(requestDto));

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(mapper.toHubRouteCreateResponseDto(responseDto));
	}

	@RequiredRoles(UserRoleType.MASTER)
	@GetMapping("/{id}")
	public ResponseEntity<?> getHubRouteById(@PathVariable UUID id) {
		HubRouteSearchAdminResponseServiceDto responseDto = hubRouteAdminService
			.getHubRouteById(mapper.toHubRouteSearchAdminRequestServiceDto(id));

		return ResponseEntity.ok(mapper.toHubRouteSearchAdminResponseDto(responseDto));
	}

	@RequiredRoles(UserRoleType.MASTER)
	@GetMapping
	public ResponseEntity<?> searchHubRoutes(
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
		@RequestParam(value = "by", required = false) String by,
		@RequestParam(value = "id", required = false) UUID id,
		@RequestParam(value = "departureHubId", required = false) UUID departureHubId,
		@RequestParam(value = "arriveHubId", required = false) UUID arriveHubId,
		@RequestParam(value = "isDeleted", required = false) Boolean isDeleted
	) {
		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);
		HubRouteSearchAdminRequestDto requestDto =
			new HubRouteSearchAdminRequestDto(id, departureHubId, arriveHubId, isDeleted);

		Page<HubRouteSearchAdminResponseServiceDto> responseDto = hubRouteAdminService
			.searchHubRoutes(mapper.toHubRoutesSearchAdminRequestServiceDto(requestDto), pageable);
		return ResponseEntity.ok(responseDto.map(mapper::toHubRouteSearchAdminResponseDto));
	}

	@RequiredRoles(UserRoleType.MASTER)
	@PutMapping("/{id}")
	public ResponseEntity<?> updateHubRoute(
		@PathVariable UUID id,
		@RequestBody HubRouteUpdateRequestDto requestDto
	) {
		HubRouteUpdateResponseServiceDto responseDto = hubRouteAdminService
			.updateHubRoute(id, mapper.toHubRouteUpdateRequestServiceDto(requestDto));

		return ResponseEntity.ok(mapper.toHubRouteUpdateResponseDto(responseDto));
	}

	@RequiredRoles(UserRoleType.MASTER)
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteHubRoute(@PathVariable UUID id, HttpServletRequest request) {
		Long userId = (Long) request.getAttribute("userId");
		hubRouteAdminService.deleteHubRoute(mapper.toHubRouteDeleteRequestServiceDto(id), userId);
		return ResponseEntity.ok().build();
	}
}
