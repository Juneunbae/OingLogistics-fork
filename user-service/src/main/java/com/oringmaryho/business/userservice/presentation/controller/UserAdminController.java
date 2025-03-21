package com.oringmaryho.business.userservice.presentation.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oringmaryho.business.userservice.application.dto.request.UserAdminCreateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminDeleteRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminDeleteRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminFindRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminGrantRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSlackCodeRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminUpdateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminUpdateRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignOutRequestServiceDto;
import com.oringmaryho.business.userservice.application.service.UserAdminService;
import com.oringmaryho.business.userservice.config.pageable.PageableConfig;
import com.oringmaryho.business.userservice.presentation.dto.mapper.UserPresentationMapper;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminCreateRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminGrantRoleRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminSearchRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminSlackCodeRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminUpdateRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminUpdateRoleRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSlackConfirmRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminGrantRoleResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminUpdateResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminUpdateRoleResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/users")
public class UserAdminController {

	private final UserAdminService userAdminService;
	private final UserPresentationMapper userPresentationMapper;
	private final PageableConfig pageableConfig;

	@PostMapping("/sign-up")
	public ResponseEntity<Void> signUpMasterUser(
		@RequestBody UserAdminSignUpRequestDto requestDto) {
		UserAdminSignUpRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminSignUpServiceDto(
			requestDto);
		userAdminService.signUpUserAdmin(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/sign-out")
	public ResponseEntity<?> signOutUser(
		@RequestAttribute("userId") Long userId
	) {
		UserSignOutRequestServiceDto requestServiceDto = userPresentationMapper.toUserSignOutRequestServiceDto(userId);
		userAdminService.signOutUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@PostMapping()
	public ResponseEntity<Void> createUser(
		@RequestBody UserAdminCreateRequestDto requestDto) {
		UserAdminCreateRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminCreateRequestServiceDto(
			requestDto);
		userAdminService.createUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> findUserMaster(@PathVariable Long id) {
		UserAdminFindRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminFindRequestServiceDto(
			id);
		return ResponseEntity.ok(userAdminService.findUserAdmin(requestServiceDto));
	}

	@GetMapping()
	public ResponseEntity<Page<UserAdminSearchResponseDto>> searchUsers(
		@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
		@RequestParam(value = "size", required = false) Integer size,
		@RequestParam(value = "sortDirection", required = false) String sortDirection,
		@RequestBody UserAdminSearchRequestDto requestDto) {
		Pageable customPageable = pageableConfig.customPageable(page, size, sortDirection);
		UserAdminSearchRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminSearchRequestServiceDto(
			requestDto, customPageable);
		Page<UserAdminSearchResponseDto> responseDtos = userAdminService.searchUsers(
			requestServiceDto, customPageable);
		return ResponseEntity.ok(responseDtos);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateUserMaster(@PathVariable Long id,
		@RequestBody UserAdminUpdateRequestDto requestDto) {
		UserAdminUpdateRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminUpdateRequestServiceDto(
			requestDto, id);
		UserAdminUpdateResponseDto responseDto = userAdminService.updateUser(requestServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@PutMapping("/{id}/grant")
	public ResponseEntity<UserAdminGrantRoleResponseDto> grantRoleUserMaster(@PathVariable Long id,
		@RequestBody UserAdminGrantRoleRequestDto requestDto) {
		UserAdminGrantRoleRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminGrantRoleRequestServiceDto(
			requestDto, id);
		UserAdminGrantRoleResponseDto responseDto = userAdminService.grantRoleUser(requestServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@PutMapping("/roles/{id}")
	public ResponseEntity<UserAdminUpdateRoleResponseDto> updateRoleUserMaster(@PathVariable Long id,
		@RequestBody UserAdminUpdateRoleRequestDto requestDto) {
		UserAdminUpdateRoleRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminUpdateRoleRequestServiceDto(
			requestDto, id);
		UserAdminUpdateRoleResponseDto responseDto = userAdminService.updateRoleUser(
			requestServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@DeleteMapping("/roles/{id}")
	public ResponseEntity<?> deleteRoleUserMaster(@PathVariable Long id) {
		UserAdminDeleteRoleRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminDeleteRoleRequestServiceDto(
			id);
		userAdminService.deleteRoleUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUserMaster(@PathVariable Long id) {
		UserAdminDeleteRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminDeleteRequestServiceDto(
			id);
		userAdminService.deleteUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	//slack 인증을 위한 코드 전송 요청
	@PostMapping("/slack/confirm-code")
	public ResponseEntity<Void> slackCodeRequestUser(
		@RequestBody UserAdminSlackCodeRequestDto requestDto) {
		UserAdminSlackCodeRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminSlackCodeRequestServiceDto(
			requestDto);
		userAdminService.slackCodeRequestUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	//slack 인증 코드로 인증 확인
	@PostMapping("/slack/confirm")
	public ResponseEntity<Void> slackConfirmUser(
		@RequestBody UserSlackConfirmRequestDto userSlackConfirmRequestDto) {
		UserAdminSlackConfirmRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminSlackConfirmRequestServiceDto(
			userSlackConfirmRequestDto);
		userAdminService.slackConfirmUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

}
