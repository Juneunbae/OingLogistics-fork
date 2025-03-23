package com.oringmaryho.business.slackservice.presentation.controller;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oringmaryho.business.slackservice.application.dto.request.SlackAdminMessageCreateRequestServiceDto;
import com.oringmaryho.business.slackservice.application.service.SlackAdminMessageService;
import com.oringmaryho.business.slackservice.config.pageable.PageableConfig;
import com.oringmaryho.business.slackservice.presentation.dto.mapper.SlackPresentationMapper;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackAdminMessageCreateRequestDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/slack-messages")
public class SlackController {

  private final SlackAdminMessageService slackAdminMessageService;
  private final SlackPresentationMapper slackPresentationMapper;
  private final PageableConfig pageableConfig;

  @Description(
      "슬랙 메세지 생성(발송 개념)"
  )
  @PostMapping
  public ResponseEntity<Void> createSlackMessage(
      @RequestBody SlackAdminMessageCreateRequestDto requestDto
  ) {
    SlackAdminMessageCreateRequestServiceDto createdMessage = slackAdminMessageService.createSlackMessage(requestDto);
    return ResponseEntity.ok().build();
  }

}
