package com.oringmaryho.business.userservice.presentation.controller;

import java.util.List;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oringmaryho.business.userservice.application.service.UserFeignService;
import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.UserRoleType;
import com.oringmaryho.business.userservice.presentation.dto.mapper.UserPresentationMapper;
import com.oringmaryho.business.userservice.application.dto.request.UsersRequestServiceDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-service/users")
public class UserFeignClientController {
	private final UserFeignService userFeignService;
	private final UserPresentationMapper userPresentationMapper;

	@Description("FeignClient - role 별 user 검색 리스트")
	@GetMapping
	public ResponseEntity<List<User>> userServiceGetByRole(
		@RequestParam(name = "role") UserRoleType role,
		@RequestParam(name = "isDeleted", required = false) Boolean isDeleted) {
		UsersRequestServiceDto usersRequestServiceDto = userPresentationMapper.toUsersRequestServiceDto(role, isDeleted);

		return ResponseEntity.ok(userFeignService.userServiceGetByRole(usersRequestServiceDto));
	}
}
