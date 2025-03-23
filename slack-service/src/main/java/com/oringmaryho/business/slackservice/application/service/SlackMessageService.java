package com.oringmaryho.business.slackservice.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import com.oringmaryho.business.slackservice.application.dto.request.SlackAdminMessageCreateRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageDeleteRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageSearchRequestServiceDto;
import com.oringmaryho.business.slackservice.application.feign.UserClient;
import com.oringmaryho.business.slackservice.application.utils.DirectMessageService;
import com.oringmaryho.business.slackservice.domain.SlackMessage;
import com.oringmaryho.business.slackservice.exception.ErrorCode;
import com.oringmaryho.business.slackservice.exception.SlackException;
import com.oringmaryho.business.slackservice.infrastructure.SlackJpaRepository;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackAdminMessageCreateRequestDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageRequestDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageUpdateResponseDto;
import com.oringmaryho.business.slackservice.presentation.dto.response.SlackMessageResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackMessageService {

	private final DirectMessageService directMessageService;
	private final UserClient userClient;
	private final SlackJpaRepository slackJpaRepository;

	@Description("모든 슬랙 메시지 조회")
	public List<SlackMessageResponseDto> getSlackMessages(SlackMessageSearchRequestServiceDto requestServiceDto) {
		return null;
	}

	@Description("id로 슬랙 메시지 조회")
	public SlackMessageResponseDto getSlackMessageById(UUID id) {
		return null;
	}

	@Description(
		"슬랙 메시지 생성: 슬랙 컨트롤러에서 받음"
	)
	public void createSlackMessage(SlackAdminMessageCreateRequestServiceDto requestDto) {

		//user service에서 슬랙 아이디 받아오기
		//인터페이스 설정
		String slackId = String.valueOf(userClient.getUserSlackIdById(requestDto.id()));
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
	public SlackAdminMessageCreateRequestServiceDto createSlackMessageFromUserService(
		SlackAdminMessageCreateRequestDto requestDto) {
		//todo: 실제 생성 로직 구현

		return null;
	}

	@Description(
		"슬랙 메시지 생성: 배송 큐에서 받음"
	)
	public SlackAdminMessageCreateRequestServiceDto createSlackMessageFromUserDelivery(
		SlackAdminMessageCreateRequestDto requestDto) {
		//todo: 실제 생성 로직 구현

		return null;
	}

	@Description(
		"슬랙 메시지 생성: 상품 큐에서 받음"
	)
	public SlackAdminMessageCreateRequestServiceDto createSlackMessageFromUserProduct(
		SlackAdminMessageCreateRequestDto requestDto) {
		//todo: 실제 생성 로직 구현

		return null;
	}

	@Description(
		"슬랙 메시지 생성: 주문 큐에서 받음"
	)
	public SlackAdminMessageCreateRequestServiceDto createSlackMessageFromUserOrder(
		SlackAdminMessageCreateRequestDto requestDto) {
		//todo: 실제 생성 로직 구현

		return null;
	}

	@Description("슬랙 메시지 수정")
	public SlackMessageUpdateResponseDto updateSlackMessage(UUID id, SlackMessageRequestDto requestDto) {
		return null;
	}

	@Description("슬랙 메시지 삭제")
	public void deleteSlackMessage(SlackMessageDeleteRequestServiceDto requestServiceDto) {

	}
}
