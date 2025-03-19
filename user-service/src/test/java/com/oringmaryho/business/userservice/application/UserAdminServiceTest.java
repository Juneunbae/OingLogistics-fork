package com.oringmaryho.business.userservice.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import com.oringmaryho.business.userservice.application.dto.request.UserAdminSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.UserRoleType;
import com.oringmaryho.business.userservice.domain.repository.UserRepository;

@SpringBootTest
@TestPropertySource(properties = "admin.key=admin-key123")
class UserAdminServiceTest {

	@InjectMocks
	private UserAdminService userAdminService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Value("${admin.key}")
	private String adminKey;

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
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			userAdminService.signUpUserAdmin(requestServiceDto);
		});
		assertEquals("사용자 이름은 비어 있을 수 없습니다.", exception.getMessage());
	}

	@Test
	void signUpUserAdmin_whenPasswordIsNull_shouldThrowException() {
		// Given
		UserAdminSignUpRequestServiceDto requestServiceDto = new UserAdminSignUpRequestServiceDto(
			"username", null, "slackId", "admin-key123");

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			userAdminService.signUpUserAdmin(requestServiceDto);
		});
		assertEquals("비밀번호는 비어 있을 수 없습니다.", exception.getMessage());
	}

	@Test
	void signUpUserAdmin_whenSlackIdIsNull_shouldThrowException() {
		// Given
		UserAdminSignUpRequestServiceDto requestServiceDto = new UserAdminSignUpRequestServiceDto(
			"username", "password123", null, "admin-key123");

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			userAdminService.signUpUserAdmin(requestServiceDto);
		});
		assertEquals("slackId는 비어 있을 수 없습니다.", exception.getMessage());
	}

	@Test
	void signUpUserAdmin_whenKeyIsNull_shouldThrowException() {
		// Given
		UserAdminSignUpRequestServiceDto requestServiceDto = new UserAdminSignUpRequestServiceDto(
			"username", "password123", "slackId", null);

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			userAdminService.signUpUserAdmin(requestServiceDto);
		});
		assertEquals("key는 비어 있을 수 없습니다.", exception.getMessage());
	}

	@Test
	void signUpUserAdmin_whenUsernameAlreadyExists_shouldThrowException() {
		// Given
		String username = "existingUsername";
		UserAdminSignUpRequestServiceDto requestServiceDto = new UserAdminSignUpRequestServiceDto(
			username, "password123", "slackId", "admin-key123");

		when(userRepository.existsByUsername(username)).thenReturn(true);

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			userAdminService.signUpUserAdmin(requestServiceDto);
		});
		assertEquals("이미 존재하는 사용자입니다.", exception.getMessage());
	}

	@Test
	void signUpUserAdmin_whenKeyDoesNotMatch_shouldThrowException() {
		// Given
		UserAdminSignUpRequestServiceDto requestServiceDto = new UserAdminSignUpRequestServiceDto(
			"username", "password123", "slackId", "wrongAdminKey");

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			userAdminService.signUpUserAdmin(requestServiceDto);
		});
		assertEquals("admin키가 일치하지 않습니다.", exception.getMessage());
	}

	@Test
	void signUpUserAdmin_whenValidRequest_shouldSaveUser() {
		// Given
		final String username = "validUsername";
		final String rawPassword = "password123";
		final String encodedPassword = "encodedPassword";
		final String slackId = "slackId";

		UserAdminSignUpRequestServiceDto requestDto = new UserAdminSignUpRequestServiceDto(
			username, rawPassword, slackId, adminKey
		);

		when(userRepository.existsByUsername(username)).thenReturn(false);
		when(passwordEncoder.encode(eq(rawPassword))).thenReturn(encodedPassword);

		// When
		userAdminService.signUpUserAdmin(requestDto);

		// Then
		verify(userRepository, times(1)).existsByUsername(username);
		verify(passwordEncoder, times(1)).encode(rawPassword);

		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository, times(1)).save(userCaptor.capture());

		User savedUser = userCaptor.getValue();
		assertAll(
			() -> assertEquals(username, savedUser.getUsername(), "Username 불일치"),
			() -> assertEquals(encodedPassword, savedUser.getPassword(), "Password 인코딩 실패"),
			() -> assertEquals(slackId, savedUser.getSlackId(), "Slack ID 불일치"),
			() -> assertEquals(UserRoleType.MASTER, savedUser.getRole(), "User role 권한 부족")
		);

		verifyNoMoreInteractions(userRepository, passwordEncoder);
	}

}

