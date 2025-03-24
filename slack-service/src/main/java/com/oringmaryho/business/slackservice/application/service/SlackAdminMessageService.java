package com.oringmaryho.business.slackservice.application.service;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oringmaryho.business.slackservice.application.dto.mapper.SlackApplicationMapper;
import com.oringmaryho.business.slackservice.application.dto.request.SlackAdminMessageCreateRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageDeleteRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageFindRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageSearchRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageUpdateRequestServiceDto;
import com.oringmaryho.business.slackservice.application.feign.UserClient;
import com.oringmaryho.business.slackservice.application.utils.DirectMessageService;
import com.oringmaryho.business.slackservice.domain.SlackMessage;
import com.oringmaryho.business.slackservice.domain.SlackMessageSearchCriteria;
import com.oringmaryho.business.slackservice.domain.repository.CustomSlackMessageRepository;
import com.oringmaryho.business.slackservice.exception.ErrorCode;
import com.oringmaryho.business.slackservice.exception.SlackException;
import com.oringmaryho.business.slackservice.infrastructure.SlackJpaRepository;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackAdminMessageCreateRequestDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageUpdateResponseDto;
import com.oringmaryho.business.slackservice.presentation.dto.response.SlackMessageResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackAdminMessageService {

	private final DirectMessageService directMessageService;
	private final UserClient userClient;
	private final SlackJpaRepository slackJpaRepository;
	private final SlackApplicationMapper slackApplicationMapper;
	private final CustomSlackMessageRepository customSlackMessageRepository;

	@Description("모든 슬랙 메시지 조회")
	@Transactional(readOnly = true)
	public Page<SlackMessageResponseDto> getSlackMessages(SlackMessageSearchRequestServiceDto requestServiceDto,
		Pageable pageable) {

		//쿼리 dsl로 유저 조회
		Page<SlackMessage> messages = customSlackMessageRepository.findDynamicQuery(
			createSlackSearchCriteria(requestServiceDto),
			pageable);

		return messages.map(slackApplicationMapper::toSlackMessageResponseDto);
	}

	public SlackMessageSearchCriteria createSlackSearchCriteria(SlackMessageSearchRequestServiceDto requestDto) {
		return SlackMessageSearchCriteria.builder()
			.id(requestDto.id())
			.receiverId(requestDto.receiverId())
			.message(requestDto.message())
			.sentAt(requestDto.sentAt())
			.isDeleted(requestDto.isDeleted())
			.build();
	}

	@Description("id로 슬랙 메시지 조회")
	@Transactional(readOnly = true)
	public SlackMessageResponseDto getSlackMessageById(SlackMessageFindRequestServiceDto requestServiceDto) {
		SlackMessage slackMessage = customSlackMessageRepository.findActiveSlackMessageById(requestServiceDto.id())
			.orElseThrow(() -> new SlackException(ErrorCode.NOT_FOUND));

		return slackApplicationMapper.toSlackMessageResponseDto(slackMessage);
	}

	@Description(
		"슬랙 메시지 생성: 슬랙 컨트롤러에서 받음"
	)
	@Transactional
	public void createSlackMessage(SlackAdminMessageCreateRequestServiceDto requestDto) {

		//user service에서 슬랙 아이디 받아오기
		//인터페이스 설정
		ResponseEntity<String> response = userClient.getUserSlackIdById(requestDto.id());
		String slackId = response.getBody();
		log.info("slackId:{}", slackId);
		//slackClient 메시지 송신 메서드 호출
		String message = requestDto.message();
		if (slackId == null || slackId.isEmpty()) {
			throw new SlackException(ErrorCode.SLACK_ID_EMPTY);
		}
		directMessageService.sendDirectMessage(slackId, message);

		//보낸 슬랙 메시지 저장
		SlackMessage slackMessage = SlackMessage.builder()
			.receiverId(requestDto.id())
			.message(message)
			.sentAt(LocalDateTime.now())
			.build();
		slackJpaRepository.save(slackMessage);
	}

	@Description(
		"슬랙 메시지 생성: 사용자 큐에서 받음"
	)
	public void createSlackMessageFromUserService(
		SlackAdminMessageCreateRequestDto requestDto) {
		//todo: 실제 생성 로직 구현
	}

	@Description(
		"슬랙 메시지 생성: 배송 큐에서 받음"
	)
	public void createSlackMessageFromUserDelivery(
		SlackAdminMessageCreateRequestDto requestDto) {
		//todo: 실제 생성 로직 구현
	}

	@Description(
		"슬랙 메시지 생성: 상품 큐에서 받음"
	)
	public void createSlackMessageFromUserProduct(
		SlackAdminMessageCreateRequestDto requestDto) {
		//todo: 실제 생성 로직 구현
	}

	@Description(
		"슬랙 메시지 생성: 주문 큐에서 받음"
	)
	public void createSlackMessageFromUserOrder(
		SlackAdminMessageCreateRequestDto requestDto) {
		//todo: 실제 생성 로직 구현
	}

	@Description("슬랙 메시지 수정")
	@Transactional
	public SlackMessageUpdateResponseDto updateSlackMessage(SlackMessageUpdateRequestServiceDto requestServiceDto) {
		SlackMessage slackMessage = slackJpaRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new SlackException(ErrorCode.NOT_FOUND));
		slackMessage.setMessage(requestServiceDto.message());
		//todo: 슬랙 메시지 수정 시 채팅창에 있는 내용도 수정
		return slackApplicationMapper.toSlackMessageUpdateResponseDto(requestServiceDto.id());
	}

	@Description("슬랙 메시지 삭제")
	@Transactional
	public void deleteSlackMessage(SlackMessageDeleteRequestServiceDto requestServiceDto) {
		SlackMessage slackMessage = slackJpaRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new SlackException(ErrorCode.NOT_FOUND));
		slackMessage.softDelete(requestServiceDto.userId());
	}
}
