package com.oringmaryho.business.slackservice.application.service;

import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageCreateRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageDeleteRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageSearchRequestServiceDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageCreateRequestDto;
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
public class SlackMessageService {

  @Description("모든 슬랙 메시지 조회")
  public List<SlackMessageResponseDto> getSlackMessages(SlackMessageSearchRequestServiceDto requestServiceDto) {
    return null;
  }

  @Description("id로 슬랙 메시지 조회")
  public SlackMessageResponseDto getSlackMessageById(UUID id) {
    return null;
  }

  @Description("슬랙 메시지 생성")
  public SlackMessageCreateRequestServiceDto createSlackMessage(SlackMessageCreateRequestDto requestDto) {
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
