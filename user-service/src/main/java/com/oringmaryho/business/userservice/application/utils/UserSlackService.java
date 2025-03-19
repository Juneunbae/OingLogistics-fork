package com.oringmaryho.business.userservice.application.utils;

import java.security.SecureRandom;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.oringmaryho.business.userservice.exception.ErrorCode;
import com.oringmaryho.business.userservice.exception.UserException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserSlackService {

	@Value("${slack.bot.token}")
	private String SLACK_TOKEN;

	@Value("${slack.userlist.url}")
	private String SLACK_USER_LIST_URL;

	@Value("${slack.conversation.url}")
	private String SLACK_CONVERSATION_URL;

	@Value("${slack.chat.url}")
	private String SLACK_CHAT_URL;

	private final RestTemplate restTemplate = new RestTemplate();

	//슬랙 id 가져오기
	public String getUserSlackId(String slackUsername) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(SLACK_TOKEN);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<String> request = new HttpEntity<>(headers);

		try {
			ResponseEntity<Map> response = restTemplate.exchange(SLACK_USER_LIST_URL, HttpMethod.GET, request,
				Map.class);
			Map<String, Object> body = response.getBody();

			if (body == null || body.get("members") == null) {
				throw new UserException(ErrorCode.SLACK_INVALID_RESPONSE);
			}

			for (Map<String, Object> user : (Iterable<Map<String, Object>>)body.get("members")) {
				if (user.get("name").equals(slackUsername)) {
					return (String)user.get("id"); // Slack 사용자 ID 반환
				}
			}
			throw new UserException(ErrorCode.NOT_FOUND);
		} catch (RestClientException e) {
			throw new UserException(ErrorCode.SLACK_API_ERROR);
		}
	}

	// DM 채널 생성
	public String openDirectMessageChannel(String userId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(SLACK_TOKEN);
		headers.setContentType(MediaType.APPLICATION_JSON);

		String payload = "{ \"users\": \"" + userId + "\" }";
		HttpEntity<String> request = new HttpEntity<>(payload, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(SLACK_CONVERSATION_URL, request, Map.class);
		Map<String, Object> channel = (Map<String, Object>)response.getBody().get("channel");

		return (String)channel.get("id"); // DM 채널 ID 반환
	}

	// 코드 DM 발송
	public void sendDirectMessage(String slackUsername, String code) {
		//슬랙 id 가져오기
		String userId = getUserSlackId(slackUsername);
		if (userId == null) {
			throw new RuntimeException("사용자 ID를 찾을 수 없습니다.");
		}

		String channelId = openDirectMessageChannel(userId);

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(SLACK_TOKEN);
		headers.setContentType(MediaType.APPLICATION_JSON);

		String payload = "{ \"channel\": \"" + channelId + "\", \"text\": \"인증 코드: " + code + "\" }";
		HttpEntity<String> request = new HttpEntity<>(payload, headers);

		restTemplate.postForEntity(SLACK_CHAT_URL, request, String.class);
	}

	public String generateCode() {
		SecureRandom random = new SecureRandom();
		int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
		return String.valueOf(code);
	}
}
