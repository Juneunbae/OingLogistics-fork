package com.oringmaryho.business.userservice.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Description;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.oringmaryho.business.userservice.application.dto.request.UserSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackCodeRequestServiceDto;
import com.oringmaryho.business.userservice.application.service.UserService;
import com.oringmaryho.business.userservice.application.utils.CodeStorage;
import com.oringmaryho.business.userservice.application.utils.DirectMessageAuthService;
import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.UserConfirmStatus;
import com.oringmaryho.business.userservice.domain.repository.UserRepository;
import com.oringmaryho.business.userservice.exception.ErrorCode;
import com.oringmaryho.business.userservice.exception.UserException;

public class UserServiceTest {

	@InjectMocks
	private UserService userService;  // 테스트 대상 클래스

	@Mock
	private UserRepository userRepository;  // 의존성 Mock

	@Mock
	private PasswordEncoder passwordEncoder;  // 의존성 Mock

	@Mock
	private DirectMessageAuthService directMessageAuthService;

	@Mock
	private CodeStorage codeStorage;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);  // Mock 객체 초기화
		ReflectionTestUtils.setField(userService, "SLACK_CODE_TTL", 300L);
	}

	@Test
	@Description(
		"username이 null일 경우 exception 테스트"
	)
	public void testSignUpUser_whenUsernameIsNull_shouldThrowException() {
		// Given
		UserSignUpRequestServiceDto requestServiceDto = new UserSignUpRequestServiceDto(null, "password123", "slackId");

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> {
			userService.signUpUser(requestServiceDto);
		});
		assertEquals(ErrorCode.USERNAME_NULL, exception.getErrorCode());
	}

	@Test
	@Description(
		"password가 null일 경우 exception 테스트"
	)
	public void testSignUpUser_whenPasswordIsNull_shouldThrowException() {
		// Given
		UserSignUpRequestServiceDto requestServiceDto = new UserSignUpRequestServiceDto("username", null, "slackId");

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> {
			userService.signUpUser(requestServiceDto);
		});
		assertEquals(ErrorCode.PASSWORD_NULL.getMessage(), exception.getMessage());
	}

	@Test
	@Description(
		"slackId가 null일 경우 exception 테스트"
	)
	public void testSignUpUser_whenSlackIdIsNull_shouldThrowException() {
		// Given
		UserSignUpRequestServiceDto requestServiceDto = new UserSignUpRequestServiceDto("username", "password", null);

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> {
			userService.signUpUser(requestServiceDto);
		});
		assertEquals(ErrorCode.SLACKID_NULL.getMessage(), exception.getMessage());
	}

	@Test
	@Description(
		"password 인코딩 테스트"
	)
	public void testSignUpUser_passwordEncoding() {
		// Given
		String rawPassword = "password1!";
		String encodedPassword = "encodedPassword123";
		UserSignUpRequestServiceDto requestServiceDto = new UserSignUpRequestServiceDto("username", rawPassword,
			"slackId");

		// When
		when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
		when(userRepository.existsByUsername(requestServiceDto.username())).thenReturn(false); // username 중복 없음

		// Then
		userService.signUpUser(requestServiceDto);

		// passwordEncoder.encode() 요청 횟수 검증
		verify(passwordEncoder, times(1)).encode(rawPassword);

		// UserRepository.save() 메서드를 호출했을 때 상태를 캡처
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());

		User savedUser = userCaptor.getValue();
		assertEquals(encodedPassword, savedUser.getPassword());  // 비밀번호가 인코딩된 값인지 확인
	}

	@Test
	@Description(
		"Slack 코드 요청 성공 케이스: " +
			"주어진 사용자 이름으로 유저가 존재하고, 상태가 PENDING이며, " +
			"Slack 코드가 생성되고 저장소에 저장되며, 이전 코드가 없는 경우 " +
			"정상적으로 Slack 메시지가 전송되고 코드가 저장되는지 검증"
	)
	void slackCodeRequestUser_success() {
		// Given
		UserSlackCodeRequestServiceDto dto = new UserSlackCodeRequestServiceDto("testUser");
		User user = User.builder()
			.username("testUser")
			.slackId("slack123")
			.status(UserConfirmStatus.PENDING)
			.build();
		when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
		when(directMessageAuthService.generateCode()).thenReturn("slackCode123");
		when(codeStorage.hasKey("testUser")).thenReturn(false);

		// When
		userService.slackCodeRequestUser(dto);

		// Then
		verify(directMessageAuthService).sendDirectMessage("slack123", "slackCode123");
		verify(codeStorage).storeCode("testUser", "slack123", "slackCode123", 300L);
		verify(codeStorage, never()).removeCode(anyString()); // 이전 코드 없으므로 삭제 호출 없음
	}

	@Test
	@Description(
		"Slack 코드 요청 실패 케이스: " +
			"주어진 사용자 이름으로 유저가 존재하지 않을 때 " +
			"UserException(NOT_FOUND)이 발생하며, " +
			"Slack 코드 생성 및 이후 로직이 실행되지 않는지 검증"
	)
	void slackCodeRequestUser_userNotFound_throwsException() {
		// Given
		UserSlackCodeRequestServiceDto dto = new UserSlackCodeRequestServiceDto("testUser");
		when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> {
			userService.slackCodeRequestUser(dto);
		});
		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		verify(directMessageAuthService, never()).generateCode(); // 이후 로직 실행 안 됨
	}

	@Test
	@Description(
		"Slack 코드 요청 실패 케이스: " +
			"유저의 username이 null일 때 " +
			"UserException(USERNAME_NULL)이 발생하며, " +
			"Slack 코드 생성 및 이후 로직이 실행되지 않는지 검증"
	)
	void slackCodeRequestUser_usernameNull_throwsException() {
		// Given
		UserSlackCodeRequestServiceDto dto = new UserSlackCodeRequestServiceDto("testUser");
		User user = User.builder()
			.username(null)
			.slackId("slack123")
			.status(UserConfirmStatus.PENDING)
			.build();
		when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> {
			userService.slackCodeRequestUser(dto);
		});
		assertEquals(ErrorCode.USERNAME_NULL, exception.getErrorCode());
	}

	@Test
	@Description(
		"Slack 코드 요청 실패 케이스: " +
			"유저의 slackId가 null일 때 " +
			"UserException(SLACKID_NULL)이 발생하며, " +
			"Slack 코드 생성 및 이후 로직이 실행되지 않는지 검증"
	)
	void slackCodeRequestUser_slackIdNull_throwsException() {
		// Given
		UserSlackCodeRequestServiceDto dto = new UserSlackCodeRequestServiceDto("testUser");
		User user = User.builder()
			.username("testUser")
			.slackId(null)
			.status(UserConfirmStatus.PENDING)
			.build();
		when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> {
			userService.slackCodeRequestUser(dto);
		});
		assertEquals(ErrorCode.SLACKID_NULL, exception.getErrorCode());
	}

	@Test
	@Description(
		"Slack 코드 요청 실패 케이스: " +
			"유저의 상태가 CONFIRMED일 때 " +
			"UserException(SLACK_ALREADY_AUTH)이 발생하며, " +
			"Slack 코드 생성 및 이후 로직이 실행되지 않는지 검증"
	)
	void slackCodeRequestUser_alreadyConfirmed_throwsException() {
		// Given
		UserSlackCodeRequestServiceDto dto = new UserSlackCodeRequestServiceDto("testUser");
		User user = User.builder()
			.username("testUser")
			.slackId("slack123")
			.status(UserConfirmStatus.CONFIRMED)
			.build();
		when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> {
			userService.slackCodeRequestUser(dto);
		});
		assertEquals(ErrorCode.SLACK_ALREADY_AUTH, exception.getErrorCode());
		verify(directMessageAuthService, never()).generateCode();
	}

	@Test
	@Description(
		"Slack 코드 요청 성공 케이스: " +
			"기존 코드가 존재하는 경우 " +
			"기존 코드를 삭제하고 새로운 Slack 코드를 생성 및 저장하며, " +
			"Slack 메시지가 전송되고 저장소에 저장되는지 검증"
	)
	void slackCodeRequestUser_existingCode_removesAndStores() {
		// Given
		UserSlackCodeRequestServiceDto dto = new UserSlackCodeRequestServiceDto("testUser");
		User user = User.builder()
			.username("testUser")
			.slackId("slack123")
			.status(UserConfirmStatus.PENDING)
			.build();
		when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
		when(directMessageAuthService.generateCode()).thenReturn("slackCode123");
		when(codeStorage.hasKey("testUser")).thenReturn(true);

		// When
		userService.slackCodeRequestUser(dto);

		// Then
		verify(codeStorage).removeCode("testUser");
		verify(directMessageAuthService).sendDirectMessage("slack123", "slackCode123");
		verify(codeStorage).storeCode("testUser", "slack123", "slackCode123", 300L);
	}
}

