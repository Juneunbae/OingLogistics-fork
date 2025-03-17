package com.oringmaryho.business.userservice.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oringmaryho.business.userservice.application.UserService;
import com.oringmaryho.business.userservice.application.dto.request.UserSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignInRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignInRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSlackConfirmRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSignInResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;
	private final UserPresentationMapper userPresentationMapper;

	@PostMapping("/sign-up")
	public ResponseEntity<Void> signUpUser(@RequestBody UserSignUpRequestDto userSignUpRequestDto) {
		UserSignUpRequestServiceDto requestServiceDto = userPresentationMapper.toUserSignUpServiceDto(
			userSignUpRequestDto);
		userService.signUpUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/sign-in")
	public ResponseEntity<UserSignInResponseDto> signInUser(
		@RequestBody UserSignInRequestDto userSignInRequestDto) {
		UserSignInRequestServiceDto requestServiceDto = userPresentationMapper.toUserSignInServiceDto(
			userSignInRequestDto);
		UserSignInResponseDto responseDto = userService.signInUser(requestServiceDto);
		return ResponseEntity.ok().body(responseDto);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserSearchResponseDto> searchUser(@PathVariable Long id) {
		UserSearchRequestServiceDto requestServiceDto = userPresentationMapper.toUserSearchRequestServiceDto(
			id);
		UserSearchResponseDto responseServiceDto = userService.searchUser(requestServiceDto);
		return ResponseEntity.ok().body(responseServiceDto);
	}

	@PostMapping("/slack/confirm")
	public ResponseEntity<Void> slackConfirmUser(
		@RequestBody UserSlackConfirmRequestDto userSlackConfirmRequestDto) {
		UserSlackConfirmRequestServiceDto requestServiceDto = userPresentationMapper.toUserSlackConfirmRequestServiceDto(
			userSlackConfirmRequestDto);
		userService.slackConfirmUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}
}
