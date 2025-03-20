package com.oringmaryho.business.userservice.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	NOT_FOUND("U-001", "해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	USERNAME_NULL("U-002", "Username이 비어 있습니다.", HttpStatus.BAD_REQUEST),
	PASSWORD_NULL("U-003", "Password가 비어 있습니다.", HttpStatus.BAD_REQUEST),
	SLACKID_NULL("U-004", "Slack Id가 비어 있습니다.", HttpStatus.BAD_REQUEST),
	ALREADY_EXISTS("U-005", "이미 존재하는 사용자입니다.", HttpStatus.BAD_REQUEST),
	USER_NOT_MATCH("U-006", "요청한 사용자와 정보가 다릅니다.", HttpStatus.BAD_REQUEST),

	SLACK_API_ERROR("U-007", "Slack API 호출 실패.", HttpStatus.BAD_REQUEST),
	SLACK_INVALID_RESPONSE("U-008", "SLACK 응답 형식이 다릅니다.", HttpStatus.BAD_REQUEST),
	SLACK_AUTH_FAIL("U-009", "SLACK 계정 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),
	SLACK_ALREADY_AUTH("U-010", "이미 인증된 SLACK 계정입니다.", HttpStatus.BAD_REQUEST),

	STORAGE_NEGATIVE_ERROR("U-011", "TTL이 음수일 수 없습니다.", HttpStatus.BAD_REQUEST),

	CANNOT_GRANT_MASTER_ROLE("U-012", "MASTER 권한은 부여할 수 없습니다.", HttpStatus.BAD_REQUEST),
	LESS_ROLE("U-013", "사용자의 권한이 부족합니다.", HttpStatus.BAD_REQUEST),
	ADMIN_REGISTER_KEY_IS_NULL("U-014", "MASTER 인증을 위한 key값이 비어 있습니다.", HttpStatus.BAD_REQUEST),
	USERNAME_REGEX_NOT_MATCH("U-015", "사용자 이름은 4~10자이며, 소문자(a-z)와 숫자(0-9)만 사용할 수 있습니다.", HttpStatus.BAD_REQUEST),
	PASSWORD_REGEX_NOT_MATCH("U-016", "비밀번호는 8~15자이며, 대소문자(A-Z, a-z), 숫자(0-9), 특수문자(!@#$%^&*)를 포함해야 합니다.", HttpStatus.BAD_REQUEST),
	ADMIN_REGISTER_KEY_NOT_MATCH("U-017", "MASTER 인증을 위한 key값이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

	JWT_REQUIRED("U-018", "로그아웃을 위해 JWT 토큰이 필요합니다.", HttpStatus.BAD_REQUEST),
	JWT_NOT_FOUND("U-019", "로그인시 생성한 JWT 토큰이 없습니다.", HttpStatus.BAD_REQUEST),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
