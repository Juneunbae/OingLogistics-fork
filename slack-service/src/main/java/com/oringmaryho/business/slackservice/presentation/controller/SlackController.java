package com.oringmaryho.business.slackservice.presentation.controller;

import com.oringmaryho.business.slackservice.application.dto.request.SlackAdminMessageCreateRequestServiceDto;
import com.oringmaryho.business.slackservice.application.service.SlackMessageService;
import com.oringmaryho.business.slackservice.presentation.dto.mapper.SlackPresentationMapper;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackAdminMessageCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/slack-messages")
public class SlackController {

  private final SlackMessageService slackMessageService;
  private final SlackPresentationMapper slackPresentationMapper;

  @Description("슬랙 메시지 생성(발송 개념)")
  @PostMapping
  public ResponseEntity<Void> createSlackMessage(
      @RequestBody SlackAdminMessageCreateRequestDto requestDto
  ) {
    // DTO 변환
    SlackAdminMessageCreateRequestServiceDto requestServiceDto =
        slackPresentationMapper.toSlackAdminMessageCreateRequestServiceDto(requestDto);

    // 슬랙 메시지 생성 서비스 호출
    slackMessageService.createSlackMessage(requestServiceDto);

    return ResponseEntity.ok().build();
  }

}
