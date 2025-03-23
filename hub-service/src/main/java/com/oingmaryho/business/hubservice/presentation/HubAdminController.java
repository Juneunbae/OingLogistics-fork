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
import com.oingmaryho.business.hubservice.application.HubAdminService;
import com.oingmaryho.business.hubservice.application.dto.response.HubCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubSearchAdminResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubUpdateResponseServiceDto;
import com.oingmaryho.business.hubservice.presentation.dto.mapper.HubPresentationMapper;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubCreateRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubSearchAdminRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubUpdateRequestDto;
import com.oingmaryho.business.hubservice.utils.PageableUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/v1/hubs")
@RequiredArgsConstructor
public class HubAdminController {

	private final HubAdminService hubAdminService;
	private final HubPresentationMapper mapper;

	@RequiredRoles(UserRoleType.MASTER)
	@GetMapping("/{id}")
	public ResponseEntity<?> getHubById(@PathVariable UUID id) {
		HubSearchAdminResponseServiceDto responseDto = hubAdminService.getHubById(mapper.toHubSearchRequestServiceDto(id));
		return ResponseEntity.ok(mapper.toHubSearchAdminResponseDto(responseDto));
	}

	@RequiredRoles(UserRoleType.MASTER)
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
		@RequestParam(value = "managerId", required = false) Long managerId,
		@RequestParam(value = "isDeleted", required = false) Boolean isDeleted
	) {
		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);
		HubSearchAdminRequestDto requestDto =
			new HubSearchAdminRequestDto(id, name, address, latitude, longitude, managerId, isDeleted);

		Page<HubSearchAdminResponseServiceDto> responseDto =
			hubAdminService.searchHubs(mapper.toHubsSearchAdminRequestServiceDto(requestDto), pageable);
		return ResponseEntity.ok(responseDto.map(mapper::toHubSearchAdminResponseDto));
	}

	@RequiredRoles(UserRoleType.MASTER)
	@PostMapping
	public ResponseEntity<?> createHub(@RequestBody HubCreateRequestDto requestDto) {
		HubCreateResponseServiceDto responseDto = hubAdminService.createHub(mapper.toHubCreateRequestServiceDto(requestDto));
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(mapper.toHubCreateResponseDto(responseDto));
	}

	@RequiredRoles(UserRoleType.MASTER)
	@PutMapping("/{id}")
	public ResponseEntity<?> updateHub(
		@PathVariable UUID id,
		@RequestBody HubUpdateRequestDto requestDto
	) {
		HubUpdateResponseServiceDto responseDto = hubAdminService.updateHub(id, mapper.toHubUpdateRequestServiceDto(requestDto));
		return ResponseEntity.ok(mapper.toHubUpdateResponseDto(responseDto));
	}

	@RequiredRoles(UserRoleType.MASTER)
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteHub(@PathVariable UUID id, HttpServletRequest request) {
		Long userId = (Long) request.getAttribute("userId");
		hubAdminService.deleteHub(mapper.toHubDeleteRequestServiceDto(id), userId);
		return ResponseEntity.ok().build();
	}

}
