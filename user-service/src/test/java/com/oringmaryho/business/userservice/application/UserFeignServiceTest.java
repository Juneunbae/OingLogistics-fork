package com.oringmaryho.business.userservice.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;

import com.oringmaryho.business.userservice.application.dto.request.UsersRequestServiceDto;
import com.oringmaryho.business.userservice.application.service.UserFeignService;
import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.UserConfirmStatus;
import com.oringmaryho.business.userservice.domain.UserRoleType;
import com.oringmaryho.business.userservice.domain.repository.CustomUserRepository;
import com.oringmaryho.business.userservice.exception.ErrorCode;
import com.oringmaryho.business.userservice.exception.UserException;

@ExtendWith(MockitoExtension.class)
class UserFeignServiceTest {

	@Mock
	private CustomUserRepository customUserRepository;

	@InjectMocks
	private UserFeignService userFeignService;

	private UsersRequestServiceDto dto;

	@BeforeEach
	void setUp() {
		// 기본 DTO 설정
		dto = new UsersRequestServiceDto(UserRoleType.DEFAULT, null); // 기본값 설정
	}

	@Test
	@Description("role이 주어졌을 때 사용자 목록을 성공적으로 반환")
	void testUserServiceGetByRole_Success() {
		// Given
		User user1 = User.builder()
			.id(1L)
			.username("user1")
			.password("encodedPass1")
			.slackId("U123")
			.role(UserRoleType.COMPANY_DELIVERY_MANAGER)
			.status(UserConfirmStatus.CONFIRMED)
			.build();
		User user2 = User.builder()
			.id(2L)
			.username("user2")
			.password("encodedPass2")
			.slackId("U124")
			.role(UserRoleType.COMPANY_DELIVERY_MANAGER)
			.status(UserConfirmStatus.CONFIRMED)
			.build();
		List<User> expectedUsers = List.of(user1, user2);
		dto = new UsersRequestServiceDto(UserRoleType.COMPANY_DELIVERY_MANAGER, false);

		when(customUserRepository.findUsersByRole(UserRoleType.COMPANY_DELIVERY_MANAGER, false))
			.thenReturn(Optional.of(expectedUsers));

		// When
		List<User> result = userFeignService.userServiceGetByRole(dto);

		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(expectedUsers, result);
		verify(customUserRepository, times(1)).findUsersByRole(UserRoleType.COMPANY_DELIVERY_MANAGER, false);
	}

	@Test
	@Description("isDeleted가 null일 때도 사용자 목록을 성공적으로 반환")
	void testUserServiceGetByRole_IsDeletedNull_Success() {
		// Given
		User user1 = User.builder()
			.id(1L)
			.username("user1")
			.password("encodedPass1")
			.slackId("U123")
			.role(UserRoleType.DEFAULT)
			.status(UserConfirmStatus.CONFIRMED)
			.build();
		List<User> expectedUsers = List.of(user1);
		dto = new UsersRequestServiceDto(UserRoleType.DEFAULT, null);

		when(customUserRepository.findUsersByRole(UserRoleType.DEFAULT, null))
			.thenReturn(Optional.of(expectedUsers));

		// When
		List<User> result = userFeignService.userServiceGetByRole(dto);

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(expectedUsers, result);
		verify(customUserRepository, times(1)).findUsersByRole(UserRoleType.DEFAULT, null);
	}

	@Test
	@Description("리포지토리가 Optional.empty()를 반환하면 UserException 발생")
	void testUserServiceGetByRole_NullResult_ThrowsException() {
		// Given
		dto = new UsersRequestServiceDto(UserRoleType.COMPANY_DELIVERY_MANAGER, false);

		when(customUserRepository.findUsersByRole(UserRoleType.COMPANY_DELIVERY_MANAGER, false))
			.thenReturn(Optional.empty());

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> {
			userFeignService.userServiceGetByRole(dto);
		});

		assertEquals(ErrorCode.ROLE_ACTIVE_USERS_NOT_FOUND, exception.getErrorCode());
		verify(customUserRepository, times(1)).findUsersByRole(UserRoleType.COMPANY_DELIVERY_MANAGER, false);
	}

	@Test
	@Description("role이 null일 때도 동작 확인")
	void testUserServiceGetByRole_RoleNull_Success() {
		// Given
		User user1 = User.builder()
			.id(1L)
			.username("user1")
			.password("encodedPass1")
			.slackId("U123")
			.role(UserRoleType.DEFAULT)
			.status(UserConfirmStatus.CONFIRMED)
			.build();
		List<User> expectedUsers = List.of(user1);
		dto = new UsersRequestServiceDto(null, false);

		when(customUserRepository.findUsersByRole(null, false))
			.thenReturn(Optional.of(expectedUsers));

		// When
		List<User> result = userFeignService.userServiceGetByRole(dto);

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(expectedUsers, result);
		verify(customUserRepository, times(1)).findUsersByRole(null, false);
	}
}
