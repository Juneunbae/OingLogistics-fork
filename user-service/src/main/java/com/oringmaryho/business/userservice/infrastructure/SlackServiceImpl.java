package com.oringmaryho.business.userservice.infrastructure;

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
import org.springframework.web.util.UriComponentsBuilder;

import com.oringmaryho.business.userservice.application.utils.DirectMessageAuthService;
import com.oringmaryho.business.userservice.exception.ErrorCode;
import com.oringmaryho.business.userservice.exception.UserException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlackServiceImpl implements DirectMessageAuthService {

	@Value("${slack.bot.token}")
	private String SLACK_TOKEN;

	@Value("${slack.user-email-url.url}")
	private String SLACK_USER_EMAIL_URL;

	@Value("${slack.conversation.url}")
	private String SLACK_CONVERSATION_URL;

	@Value("${slack.chat.url}")
	private String SLACK_CHAT_URL;

	private final RestTemplate restTemplate = new RestTemplate();

	public String getUserIdByEmail(String userEmail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(SLACK_TOKEN);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String url = UriComponentsBuilder
			.fromUriString(SLACK_USER_EMAIL_URL)
			.queryParam("email", userEmail)
			.build()
			.toUriString();
		HttpEntity<String> request = new HttpEntity<>(headers);

		try {
			ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

			Map<String, Object> body = response.getBody();

			if (body == null || !Boolean.TRUE.equals(body.get("ok"))) {
				throw new UserException(ErrorCode.SLACK_INVALID_RESPONSE);
			}
			Map<String, Object> user = (Map<String, Object>)body.get("user");
			if (user == null) {
				throw new UserException(ErrorCode.NOT_FOUND);
			}
			return (String)user.get("id");
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
	public void sendDirectMessage(String userEmail, String code) {
		//슬랙 계정 id 가져오기
		String userId = getUserIdByEmail(userEmail);
		if (userId == null) {
			throw new RuntimeException("사용자 ID를 찾을 수 없습니다.");
		}

		String channelId = openDirectMessageChannel(userId);

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(SLACK_TOKEN);
		headers.setContentType(MediaType.APPLICATION_JSON);

		String message = String.format(
			"안녕하세요, 스파르타 물류입니다.\n" +
				"슬랙 ID 인증을 위해 아래 인증 코드를 사용해 주세요.\n\n" +
				"*인증 코드*: `%s`\n\n" +
				"이 코드는 5분간 유효합니다. 인증이 완료되면 물류 시스템에 접근할 수 있습니다.\n" +
				"문의 사항은 OingMaryho@sparta-logistics.com으로 연락 부탁드립니다.",
			code
		);
		String payload = String.format("{\"channel\": \"%s\", \"text\": \"%s\"}", channelId, message);
		HttpEntity<String> request = new HttpEntity<>(payload, headers);

		restTemplate.postForEntity(SLACK_CHAT_URL, request, String.class);
	}

	public String generateCode() {
		SecureRandom random = new SecureRandom();
		int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
		return String.valueOf(code);
	}
}
