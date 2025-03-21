package com.oringmaryho.business.userservice.presentation.controller;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oringmaryho.business.userservice.application.dto.request.UserSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignInRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignOutRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackCodeRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.application.service.UserService;
import com.oringmaryho.business.userservice.presentation.dto.mapper.UserPresentationMapper;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignInRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSlackCodeRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSlackConfirmRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSignInResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;
	private final UserPresentationMapper userPresentationMapper;

	@Description(
		"username, password, slackId를 입력 받아 회원가입"
	)
	@PostMapping("/sign-up")
	public ResponseEntity<Void> signUpUser(@RequestBody UserSignUpRequestDto requestDto) {
		UserSignUpRequestServiceDto requestServiceDto = userPresentationMapper.toUserSignUpServiceDto(
			requestDto);
		userService.signUpUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@Description(
		"username, password를 입력받아 로그인"
	)
	@PostMapping("/sign-in")
	public ResponseEntity<UserSignInResponseDto> signInUser(
		@RequestBody UserSignInRequestDto requestDto) {
		UserSignInRequestServiceDto requestServiceDto = userPresentationMapper.toUserSignInServiceDto(
			requestDto);
		UserSignInResponseDto responseDto = userService.signInUser(requestServiceDto);
		return ResponseEntity.ok().body(responseDto);
	}

	@PostMapping("/sign-out")
	@Description(
		"로그인했던 사용자 id를 받아 로그아웃"
	)
	public ResponseEntity<?> signOutUser(
		@RequestAttribute("userId") Long userId
	) {
		UserSignOutRequestServiceDto requestServiceDto = userPresentationMapper.toUserSignOutRequestServiceDto(userId);
		userService.signOutUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}")
	@Description(
		"user id 로 사용자 정보 단일 조회"
	)
	public ResponseEntity<UserSearchResponseDto> searchUser(
		@PathVariable Long id,
		@RequestAttribute("userId") Long userId
	) {
		log.info("search user id:{} userId:{}", id, userId);
		UserSearchRequestServiceDto requestServiceDto = userPresentationMapper.toUserSearchRequestServiceDto(
			id, userId);
		UserSearchResponseDto responseDto = userService.searchUser(requestServiceDto);
		log.info(responseDto.toString());
		return ResponseEntity.ok(responseDto);
	}

	@PostMapping("/slack/confirm-code")
	@Description(
		"slack 인증을 위한 코드 전송 요청"
	)
	public ResponseEntity<Void> slackCodeRequestUser(
		@RequestBody UserSlackCodeRequestDto requestDto) {
		UserSlackCodeRequestServiceDto requestServiceDto = userPresentationMapper.toUserSlackCodeRequestServiceDto(
			requestDto);
		userService.slackCodeRequestUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/slack/confirm")
	@Description(
		"slack 인증 코드로 인증 확인"
	)
	public ResponseEntity<Void> slackConfirmUser(
		@RequestBody UserSlackConfirmRequestDto requestDto) {
		UserSlackConfirmRequestServiceDto requestServiceDto = userPresentationMapper.toUserSlackConfirmRequestServiceDto(
			requestDto);
		userService.slackConfirmUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}
}
