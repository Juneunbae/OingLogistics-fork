package com.oringmaryho.business.slackservice.presentation.controller;

import com.oingmaryho.business.common.domain.type.UserRoleType;
import com.oingmaryho.business.common.infrastructure.annotation.RequiredRoles;
import java.util.UUID;

import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oringmaryho.business.slackservice.application.dto.request.SlackAdminMessageCreateRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageDeleteRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageFindRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageSearchRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageUpdateRequestServiceDto;
import com.oringmaryho.business.slackservice.application.service.SlackAdminMessageService;
import com.oringmaryho.business.slackservice.config.pageable.PageableConfig;
import com.oringmaryho.business.slackservice.presentation.dto.mapper.SlackPresentationMapper;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackAdminMessageCreateRequestDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageUpdateRequestDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageSearchRequestDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageUpdateResponseDto;
import com.oringmaryho.business.slackservice.presentation.dto.response.SlackMessageResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/slack-messages")
public class SlackAdminController {

	private final SlackAdminMessageService slackAdminMessageService;
	private final SlackPresentationMapper slackPresentationMapper;
	private final PageableConfig pageableConfig;

	@Description(
		"슬랙 메세지 전체 조회"
	)
	@RequiredRoles(UserRoleType.MASTER)
	@GetMapping
	public ResponseEntity<Page<SlackMessageResponseDto>> getSlackMessages(
		@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
		@RequestParam(value = "size", required = false) Integer size,
		@RequestParam(value = "sortDirection", required = false) String sortDirection,
		@ModelAttribute SlackMessageSearchRequestDto requestDto) {
		Pageable customPageable = pageableConfig.customPageable(page, size, sortDirection);

		SlackMessageSearchRequestServiceDto requestServiceDto = slackPresentationMapper.toSlackMessageSearchRequestServiceDto(
			requestDto);
		Page<SlackMessageResponseDto> responseDtos = slackAdminMessageService.getSlackMessages(requestServiceDto, customPageable);

		return ResponseEntity.ok(responseDtos);
	}

	@Description(
		"슬랙 메세지 상세 조회"
	)
	@RequiredRoles(UserRoleType.MASTER)
	@GetMapping("/{id}")
	public ResponseEntity<SlackMessageResponseDto> getSlackMessageById(
		@PathVariable UUID id
	) {
		SlackMessageFindRequestServiceDto requestServiceDto = slackPresentationMapper.toSlackMessageFindRequestServiceDto(
			id);
		SlackMessageResponseDto message = slackAdminMessageService.getSlackMessageById(requestServiceDto);
		return ResponseEntity.ok(message);
	}

	@Description(
		"슬랙 메세지 생성"
	)
	@RequiredRoles(UserRoleType.MASTER)
	@PostMapping
	public ResponseEntity<Void> createSlackMessage(
		@RequestBody SlackAdminMessageCreateRequestDto requestDto
	) {
		log.info("slac admin message create: {}", requestDto);
		SlackAdminMessageCreateRequestServiceDto requestServiceDto = slackPresentationMapper.toSlackAdminMessageCreateRequestServiceDto(
			requestDto);
		slackAdminMessageService.createSlackMessage(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@Description(
		"슬랙 메세지 수정"
	)
	@RequiredRoles(UserRoleType.MASTER)
	@PutMapping("/{id}")
	public ResponseEntity<SlackMessageUpdateResponseDto> updateSlackMessage(
		@PathVariable UUID id,
		@RequestBody SlackMessageUpdateRequestDto requestDto
	) {
		SlackMessageUpdateRequestServiceDto requestServiceDto = slackPresentationMapper.toSlackMessageUpdateRequestServiceDto(
			id, requestDto);
		SlackMessageUpdateResponseDto updatedMessage = slackAdminMessageService.updateSlackMessage(requestServiceDto);
		return ResponseEntity.ok(updatedMessage);
	}

	@Description(
		"슬랙 메세지 삭제"
	)
	@RequiredRoles(UserRoleType.MASTER)
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSlackMessage(
		@PathVariable UUID id,
		HttpServletRequest request
	) {
		Long userId = (Long) request.getAttribute("userId");
		SlackMessageDeleteRequestServiceDto requestServiceDto = slackPresentationMapper.toSlackMessageDeleteRequestServiceDto(
			userId, id);
		slackAdminMessageService.deleteSlackMessage(requestServiceDto);
		return ResponseEntity.ok().build();
	}
}
