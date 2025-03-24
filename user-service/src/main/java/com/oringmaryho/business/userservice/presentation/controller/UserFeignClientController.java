package com.oringmaryho.business.userservice.presentation.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oringmaryho.business.userservice.application.dto.request.UserFromDeliveryRequestServiceDto;
import com.oringmaryho.business.userservice.application.service.UserFeignService;
import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.UserRoleType;
import com.oringmaryho.business.userservice.presentation.dto.mapper.UserPresentationMapper;
import com.oringmaryho.business.userservice.application.dto.request.UsersRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.SlackGetSlackIdRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserFromDeliveryGetRoleRequestServiceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-service/users")
public class UserFeignClientController {
	private final UserFeignService userFeignService;
	private final UserPresentationMapper userPresentationMapper;

	@Description("FeignClient - role 별 user 검색 리스트")
	@GetMapping()
	public ResponseEntity<List<User>> userFeignServiceGetByRole(
		@RequestParam(name = "role") UserRoleType role,
		@RequestParam(name = "isDeleted", required = false) Boolean isDeleted) {
		UsersRequestServiceDto usersRequestServiceDto = userPresentationMapper.toUsersRequestServiceDto(role, isDeleted);

		return ResponseEntity.ok(userFeignService.userServiceGetByRole(usersRequestServiceDto));
	}

	@Description("FeignClient - 배송 서비스에서 요청, 배송 담당자 별 user 검색 리스트")
	@GetMapping("/delivery-role")
	public ResponseEntity<Map<UserRoleType, List<User>>> userFeignServiceGetByDeliveryRole(
		@RequestParam(name = "isDeleted", required = false) Boolean isDeleted) {
		UserFromDeliveryRequestServiceDto usersRequestServiceDto = userPresentationMapper.toUserFromDeliveryRequestServiceDto(isDeleted);

		return ResponseEntity.ok(userFeignService.userServiceToDeliveryServiceMap(usersRequestServiceDto));
	}

	@Description("FeignClient - 배송 서비스에서 요청, User Id를 받아 user role 조회 api")
	@GetMapping("/role")
	public ResponseEntity<UserRoleType> userFeignServiceGetRoleById(
		@RequestParam(name = "id", required = true) Long id
	){
		UserFromDeliveryGetRoleRequestServiceDto requestServiceDto = userPresentationMapper.toUserFromDeliveryGetRoleRequestServiceDto(id);

		return ResponseEntity.ok(userFeignService.userFeignServiceGetRole(requestServiceDto));
	}

	@Description("FeignClient - 슬랙 서비스에서 요청, User Id를 받아 user slackId 조회 api")
	@GetMapping("/slackId")
	public ResponseEntity<String> userFeignServiceGetSlackIdById(
		@RequestParam(name = "id", required = true) Long id
	){
		log.info("userFeignServiceGetSlackIdById");
		SlackGetSlackIdRequestServiceDto requestServiceDto = userPresentationMapper.toSlackGetSlackIdRequestServiceDto(id);

		return ResponseEntity.ok(userFeignService.userFeignServiceGetSlackId(requestServiceDto));
	}

	@Description("FeignClient - 슬랙 서비스에서 요청, User Id를 받아 user name 조회 api")
	@GetMapping("/name")
	public ResponseEntity<String> userFeignServiceGetUsernameIdById(
			@RequestParam(name = "id", required = true) Long id
	){
		log.info("userFeignServiceGetUsernameIdById request : ${}", id);

		return ResponseEntity.ok(userFeignService.userFeignServiceGetUsername(id));
	}

}
