package com.oingmaryho.business.hubservice.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oingmaryho.business.hubservice.application.HubAdminService;
import com.oingmaryho.business.hubservice.application.dto.response.HubCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.presentation.dto.mapper.HubPresentationMapper;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubCreateRequestDto;

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
}
