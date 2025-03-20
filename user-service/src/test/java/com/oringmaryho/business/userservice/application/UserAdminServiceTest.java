package com.oringmaryho.business.userservice.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.oringmaryho.business.userservice.application.dto.request.UserAdminCreateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.service.UserAdminService;
import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.repository.UserRepository;
import com.oringmaryho.business.userservice.exception.ErrorCode;
import com.oringmaryho.business.userservice.exception.UserException;

class UserAdminServiceTest {

	@InjectMocks
	private UserAdminService userAdminService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	private String adminKey = "admin-key123";

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void signUpUserAdmin_whenUsernameIsNull_shouldThrowException() {
		// Given
		UserAdminSignUpRequestServiceDto requestServiceDto = new UserAdminSignUpRequestServiceDto(
			null, "password123", "slackId", "admin-key123");

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> {
			userAdminService.signUpUserAdmin(requestServiceDto);
		});
		assertEquals(ErrorCode.USERNAME_NULL.getMessage(), exception.getMessage());
	}

	@Test
	void signUpUserAdmin_whenPasswordIsNull_shouldThrowException() {
		// Given
		UserAdminSignUpRequestServiceDto requestServiceDto = new UserAdminSignUpRequestServiceDto(
			"username", null, "slackId", "admin-key123");

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> {
			userAdminService.signUpUserAdmin(requestServiceDto);
		});
		assertEquals(ErrorCode.PASSWORD_NULL.getMessage(), exception.getMessage());
	}

	@Test
	void signUpUserAdmin_whenSlackIdIsNull_shouldThrowException() {
		// Given
		UserAdminSignUpRequestServiceDto requestServiceDto = new UserAdminSignUpRequestServiceDto(
			"username", "password123", null, "admin-key123");

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> {
			userAdminService.signUpUserAdmin(requestServiceDto);
		});
		assertEquals(ErrorCode.SLACKID_NULL.getMessage(), exception.getMessage());
	}

	@Test
	void signUpUserAdmin_whenKeyIsNull_shouldThrowException() {
		// Given
		UserAdminSignUpRequestServiceDto requestServiceDto = new UserAdminSignUpRequestServiceDto(
			"username", "password123", "slackId", null);

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> {
			userAdminService.signUpUserAdmin(requestServiceDto);
		});
		assertEquals(ErrorCode.ADMIN_REGISTER_KEY_IS_NULL.getMessage(), exception.getMessage());
	}

	@Test
	void signUpUserAdmin_whenUsernameAlreadyExists_shouldThrowException() {
		// Given
		String username = "user01";
		UserAdminSignUpRequestServiceDto requestServiceDto = new UserAdminSignUpRequestServiceDto(
			username, "password1!", "slackId", adminKey);

		when(userRepository.existsByUsername(username)).thenReturn(true);

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> {
			userAdminService.signUpUserAdmin(requestServiceDto);
		});
		assertEquals(ErrorCode.ALREADY_EXISTS.getMessage(), exception.getMessage());
	}

	@Test
	void signUpUserAdmin_whenKeyDoesNotMatch_shouldThrowException() {
		// Given
		UserAdminSignUpRequestServiceDto requestServiceDto = new UserAdminSignUpRequestServiceDto(
			"username", "password1!", "slackId", "wrongAdminKey");

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> {
			userAdminService.signUpUserAdmin(requestServiceDto);
		});
		assertEquals(ErrorCode.ADMIN_REGISTER_KEY_NOT_MATCH.getMessage(), exception.getMessage());
	}

	@Test
	void createUser_success() {
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto(
			"testuser1",
			"Password1!@#",
			"slack123"
		);
		when(userRepository.existsByUsername("testuser1")).thenReturn(false);
		when(passwordEncoder.encode("Password1!@#")).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(mock(User.class));

		assertDoesNotThrow(() -> userAdminService.createUser(dto));

		verify(userRepository, times(1)).existsByUsername("testuser1");
		verify(passwordEncoder, times(1)).encode("Password1!@#");
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void createUser_usernameNull_throwsException() {
		// Given: username이 null
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto(
			null,
			"Password123!@#",
			"slack123"
		);

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> userAdminService.createUser(dto));
		assertEquals(ErrorCode.USERNAME_NULL, exception.getErrorCode());
	}

	@Test
	void createUser_usernameEmpty_throwsException() {
		// Given: username이 빈 문자열
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto(
			"",
			"Password123!@#",
			"slack123"
		);

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> userAdminService.createUser(dto));
		assertEquals(ErrorCode.USERNAME_NULL, exception.getErrorCode());
	}

	@Test
	void createUser_passwordNull_throwsException() {
		// Given: password가 null
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto(
			"testuser123",
			null,
			"slack123"
		);

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> userAdminService.createUser(dto));
		assertEquals(ErrorCode.PASSWORD_NULL, exception.getErrorCode());
	}

	@Test
	void createUser_slackIdEmpty_throwsException() {
		// Given: slackId가 빈 문자열
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto(
			"testuser123",
			"Password123!@#",
			""
		);

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> userAdminService.createUser(dto));
		assertEquals(ErrorCode.SLACKID_NULL, exception.getErrorCode());
	}

	@Test
	void createUser_usernameInvalidFormat_throwsException() {
		// Given: username이 정규 표현식에 맞지 않음 (대문자 포함)
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto(
			"TestUser123",           // 대문자 포함
			"Password123!@#",
			"slack123"
		);

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> userAdminService.createUser(dto));
		assertEquals(ErrorCode.USERNAME_REGEX_NOT_MATCH, exception.getErrorCode());
	}

	@Test
	void createUser_passwordInvalidFormat_throwsException() {
		// Given: password가 정규 표현식에 맞지 않음 (특수문자 없음)
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto(
			"testuser1",
			"Password123", // 특수문자 없음
			"slack123"
		);

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> userAdminService.createUser(dto));
		assertEquals(ErrorCode.PASSWORD_REGEX_NOT_MATCH, exception.getErrorCode());
	}

	@Test
	void createUser_usernameAlreadyExists_throwsException() {
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto(
			"testuser1",
			"Password1!@#",
			"slack123"
		);
		when(userRepository.existsByUsername("testuser1")).thenReturn(true);
		System.out.println("Mock exists: " + userRepository.existsByUsername("testuser1"));
		System.out.println(dto.username());
		UserException exception = assertThrows(UserException.class, () -> userAdminService.createUser(dto));
		assertEquals(ErrorCode.ALREADY_EXISTS, exception.getErrorCode());
	}
}

