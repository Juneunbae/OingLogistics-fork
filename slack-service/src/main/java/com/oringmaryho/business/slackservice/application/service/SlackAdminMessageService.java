package com.oringmaryho.business.slackservice.application.service;

import com.oringmaryho.business.slackservice.application.dto.request.SlackAdminMessageCreateRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageDeleteRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageSearchRequestServiceDto;
import com.oringmaryho.business.slackservice.infrastructure.SlackClient;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackAdminMessageCreateRequestDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageRequestDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageUpdateResponseDto;
import com.oringmaryho.business.slackservice.presentation.dto.response.SlackMessageResponseDto;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackAdminMessageService {

  private final SlackClient slackClient;

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
  public SlackAdminMessageCreateRequestServiceDto createSlackMessage(SlackAdminMessageCreateRequestServiceDto requestDto) {
    //todo: 실제 생성 로직 구현
    //
    //메시지 String 템플릿 생성
    //slackClient 메시지 송신 메서드 호출
    String slackId = null;
    String message = requestDto.message();
    slackClient.sendSlackMessage(slackId, message);
    return null;
  }

  @Description(
      "슬랙 메시지 생성: 사용자 큐에서 받음"
  )
  public SlackAdminMessageCreateRequestServiceDto createSlackMessageFromUserService(SlackAdminMessageCreateRequestDto requestDto) {
    //todo: 실제 생성 로직 구현

    return null;
  }

  @Description(
      "슬랙 메시지 생성: 배송 큐에서 받음"
  )
  public SlackAdminMessageCreateRequestServiceDto createSlackMessageFromUserDelivery(SlackAdminMessageCreateRequestDto requestDto) {
    //todo: 실제 생성 로직 구현

    return null;
  }

  @Description(
      "슬랙 메시지 생성: 상품 큐에서 받음"
  )
  public SlackAdminMessageCreateRequestServiceDto createSlackMessageFromUserProduct(SlackAdminMessageCreateRequestDto requestDto) {
    //todo: 실제 생성 로직 구현

    return null;
  }

  @Description(
      "슬랙 메시지 생성: 주문 큐에서 받음"
  )
  public SlackAdminMessageCreateRequestServiceDto createSlackMessageFromUserOrder(SlackAdminMessageCreateRequestDto requestDto) {
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
