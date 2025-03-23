package com.oringmaryho.business.slackservice.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Description;
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
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageRequestDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageSearchRequestDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageUpdateResponseDto;
import com.oringmaryho.business.slackservice.presentation.dto.response.SlackMessageResponseDto;

import lombok.RequiredArgsConstructor;

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
	@GetMapping
	public ResponseEntity<List<SlackMessageResponseDto>> getSlackMessages(
		@RequestParam(required = false, defaultValue = "0") Integer page,
		@RequestParam(required = false, defaultValue = "10") Integer size,
		@RequestParam(required = false, defaultValue = "desc") String sortDirection,
		@RequestParam(required = false, defaultValue = "createdAt") String by,
		@ModelAttribute SlackMessageSearchRequestDto requestDto) {
		//todo: 전체조회 파라미터 조정
		Pageable customPageable = pageableConfig.customPageable(page, size, sortDirection);
		SlackMessageSearchRequestServiceDto requestServiceDto = slackPresentationMapper.toSlackMessageSearchRequestServiceDto(
			requestDto, customPageable);
		List<SlackMessageResponseDto> responseDtos = slackAdminMessageService.getSlackMessages(requestServiceDto);
		return ResponseEntity.ok(responseDtos);
	}

	@Description(
		"슬랙 메세지 상세 조회"
	)
	@GetMapping("/{id}")
	public ResponseEntity<SlackMessageResponseDto> getSlackMessageById(
		@PathVariable UUID id
	) {
		SlackMessageFindRequestServiceDto requestServiceDto = slackPresentationMapper.toSlackMessageFindRequestServiceDto(
			id);
		SlackMessageResponseDto message = slackAdminMessageService.getSlackMessageById(id);
		return ResponseEntity.ok(message);
	}

	@Description(
		"슬랙 메세지 생성"
	)
	@PostMapping
	public ResponseEntity<Void> createSlackMessage(
		@RequestBody SlackAdminMessageCreateRequestDto requestDto
	) {
		SlackAdminMessageCreateRequestServiceDto requestServiceDto = slackPresentationMapper.toSlackAdminMessageCreateRequestServiceDto(
			requestDto);
		slackAdminMessageService.createSlackMessage(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@Description(
		"슬랙 메세지 수정"
	)
	@PutMapping("/{id}")
	public ResponseEntity<SlackMessageUpdateResponseDto> updateSlackMessage(
		@PathVariable UUID id,
		@RequestBody SlackMessageRequestDto requestDto
	) {
		SlackMessageUpdateRequestServiceDto requestServiceDto = slackPresentationMapper.toSlackMessageUpdateRequestServiceDto(
			requestDto);
		SlackMessageUpdateResponseDto updatedMessage = slackAdminMessageService.updateSlackMessage(id, requestDto);
		return ResponseEntity.ok(updatedMessage);
	}

	@Description(
		"슬랙 메세지 삭제"
	)
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSlackMessage(
		@PathVariable UUID id
	) {
		SlackMessageDeleteRequestServiceDto requestServiceDto = slackPresentationMapper.toSlackMessageDeleteRequestServiceDto(
			id);
		slackAdminMessageService.deleteSlackMessage(requestServiceDto);
		return ResponseEntity.ok().build();
	}
}
