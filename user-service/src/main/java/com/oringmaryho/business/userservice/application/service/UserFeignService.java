package com.oringmaryho.business.userservice.application.service;

import java.util.List;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import com.oringmaryho.business.userservice.application.dto.request.UsersRequestServiceDto;
import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.repository.CustomUserRepository;
import com.oringmaryho.business.userservice.exception.ErrorCode;
import com.oringmaryho.business.userservice.exception.UserException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFeignService {
	private final CustomUserRepository customUserRepository;

	@Description(
		"권한과 삭제 여부를 받아(삭제 여부는 선택) 권한의 모든 사용자를 검색"
	)
	public List<User> userServiceGetByRole(UsersRequestServiceDto usersRequestServiceDto) {
		return customUserRepository.findUsersByRole(usersRequestServiceDto.role(), usersRequestServiceDto.isDeleted())
			.orElseThrow(() -> new UserException(ErrorCode.ROLE_ACTIVE_USERS_NOT_FOUND));
	}


	//todo:HUB_DELIVERY_MANAGER, COMPANY_DELIVERY_MANAGER묶어서 보내는 메서드

}
