package com.oingmaryho.business.hubservice.presentation;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oingmaryho.business.hubservice.application.HubAdminService;
import com.oingmaryho.business.hubservice.application.dto.response.HubCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubUpdateResponseServiceDto;
import com.oingmaryho.business.hubservice.presentation.dto.mapper.HubPresentationMapper;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubCreateRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/v1/hubs")
@RequiredArgsConstructor
public class HubAdminController {

	private final HubAdminService hubAdminService;
	private final HubPresentationMapper mapper;

	@PostMapping // TODO : 권한 확인하기
	public ResponseEntity<?> createHub(@RequestBody HubCreateRequestDto requestDto) {
		HubCreateResponseServiceDto responseDto = hubAdminService.createHub(mapper.toHubCreateRequestServiceDto(requestDto));
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(mapper.toHubCreateResponseDto(responseDto));
	}

	@PostMapping("/{id}") // TODO : 권한 확인하기
	public ResponseEntity<?> updateHub(
		@PathVariable UUID id,
		@RequestBody HubUpdateRequestDto requestDto
	) {
		HubUpdateResponseServiceDto responseDto = hubAdminService.updateHub(id, mapper.toHubUpdateRequestServiceDto(requestDto));
		return ResponseEntity.ok(mapper.toHubUpdateResponseDto(responseDto));
	}

	@DeleteMapping("/{id}") // TODO : 권한 확인하기
	public ResponseEntity<?> deleteHub(@PathVariable UUID id) {
		hubAdminService.deleteHub(mapper.toHubDeleteRequestServiceDto(id));
		return ResponseEntity.ok().build();
	}

}
