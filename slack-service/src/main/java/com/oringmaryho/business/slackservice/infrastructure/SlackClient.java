package com.oringmaryho.business.slackservice.infrastructure;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlackClient {

	public void sendSlackMessage(String slackId, String message) {
		//todo: 슬랙 메시지 전송
	}
}