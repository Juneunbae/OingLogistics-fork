package com.oringmaryho.business.userservice.presentation;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oringmaryho.business.userservice.application.UserAdminService;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminCreateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminFindRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminGrantRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminUpdateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminUpdateRoleRequestServiceDto;
import com.oringmaryho.business.userservice.config.pageable.PageableConfig;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminCreateRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminDeleteRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminGrantRoleRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminSearchRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminDeleteRoleRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminUpdateRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserAdminUpdateRoleRequestDto;
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
	public ResponseEntity<?> signUpMasterUser(
		@RequestBody UserAdminSignUpRequestDto userAdminSignUpRequestDto) {
		UserAdminSignUpRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminSignUpServiceDto(
			userAdminSignUpRequestDto);
		userAdminService.signUpUserMaster(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@PostMapping("")
	public ResponseEntity<?> createUser(
		@RequestBody UserAdminCreateRequestDto userAdminCreateRequestDto) {
		UserAdminCreateRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminCreateRequestServiceDto(
			userAdminCreateRequestDto);
		userAdminService.createUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> findUserMaster(@PathVariable Long id) {
		UserAdminFindRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminFindRequestServiceDto(
			id);
		//todo: responsedto로 변환
		userAdminService.findUserMaster(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/users")
	public ResponseEntity<List<UserAdminSearchResponseDto>> searchUsers(
		@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
		@RequestParam(value = "size", required = false) Integer size,
		@RequestParam(value = "sortDirection", required = false) String sortDirection,
		@RequestBody UserAdminSearchRequestDto userAdminSearchRequestDto) {
		Pageable customPageable = pageableConfig.customPageable(page, size, sortDirection);
		UserAdminSearchRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminSearchRequestServiceDto(
			userAdminSearchRequestDto, customPageable);
		List<UserAdminSearchResponseDto> responseDtos = userAdminService.searchUsers(
			requestServiceDto);
		return null;
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateUserMaster(@PathVariable Long id,
		@RequestBody UserAdminUpdateRequestDto userAdminUpdateRequestDto) {
		UserAdminUpdateRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminUpdateRequestServiceDto(
			userAdminUpdateRequestDto, id);
		UserAdminUpdateResponseDto responseDto = userAdminService.updateUser(requestServiceDto);
		//todo: responsedto 반환하기
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}/grant")
	public ResponseEntity<?> grantRoleUserMaster(@PathVariable Long id,
		@RequestBody UserAdminGrantRoleRequestDto userAdminGrantRoleRequestDto) {
		UserAdminGrantRoleRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminGrantRoleRequestServiceDto(
			userAdminGrantRoleRequestDto, id);
		UserAdminGrantRoleResponseDto responseDto = userAdminService.grantRoleUser(requestServiceDto);
		//todo: responsedto 반환하기
		return ResponseEntity.ok().build();
	}

	@PutMapping("/roles/{id}")
	public ResponseEntity<?> updateRoleUserMaster(@PathVariable Long id,
		@RequestBody UserAdminUpdateRoleRequestDto userAdminUpdateRoleRequestDto) {
		UserAdminUpdateRoleRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminUpdateRoleRequestServiceDto(
			userAdminUpdateRoleRequestDto, id);
		UserAdminUpdateRoleResponseDto responseDto = userAdminService.updateRoleUser(
			requestServiceDto);
		//todo: responsedto 반환하기
		return ResponseEntity.ok().build();
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
}
