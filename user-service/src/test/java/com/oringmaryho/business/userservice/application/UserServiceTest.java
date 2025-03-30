package com.oringmaryho.business.userservice.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.oringmaryho.business.userservice.application.messaging.UserMessageService;
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
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserHelper userHelper;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private DirectMessageAuthService directMessageAuthService;

	@Mock
	private UserMessageService userMessageService;

	@Mock
	private CodeStorage codeStorage;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(userService, "SLACK_CODE_TTL", 300L);
	}

	@Test
	@Description(
		"username이 null일 경우 exception 테스트"
	)
	public void testSignUpUser_whenUsernameIsNull_shouldThrowException() {
		UserSignUpRequestServiceDto dto = new UserSignUpRequestServiceDto(null, "password123", "slackId");
		doThrow(new UserException(ErrorCode.USERNAME_NULL)).when(userHelper).validateRequiredField(null, ErrorCode.USERNAME_NULL);

		UserException exception = assertThrows(UserException.class, () -> userService.signUpUser(dto));
		assertEquals(ErrorCode.USERNAME_NULL, exception.getErrorCode());
	}

	@Test
	@Description(
		"password가 null일 경우 exception 테스트"
	)
	public void testSignUpUser_whenPasswordIsNull_shouldThrowException() {
		UserSignUpRequestServiceDto dto = new UserSignUpRequestServiceDto("username", null, "slackId");
		doThrow(new UserException(ErrorCode.PASSWORD_NULL)).when(userHelper).validateRequiredField(null, ErrorCode.PASSWORD_NULL);

		UserException exception = assertThrows(UserException.class, () -> userService.signUpUser(dto));
		assertEquals(ErrorCode.PASSWORD_NULL, exception.getErrorCode());
	}

	@Test
	@Description(
		"slackId가 null일 경우 exception 테스트"
	)
	public void testSignUpUser_whenSlackIdIsNull_shouldThrowException() {
		UserSignUpRequestServiceDto dto = new UserSignUpRequestServiceDto("username", "password", null);
		doThrow(new UserException(ErrorCode.SLACKID_NULL)).when(userHelper).validateRequiredField(null, ErrorCode.SLACKID_NULL);

		UserException exception = assertThrows(UserException.class, () -> userService.signUpUser(dto));
		assertEquals(ErrorCode.SLACKID_NULL, exception.getErrorCode());
	}

	@Test
	@Description(
		"password 인코딩 테스트"
	)
	public void testSignUpUser_passwordEncoding_savesUser() {
		String rawPassword = "password1!";
		String encodedPassword = "encodedPassword123";
		UserSignUpRequestServiceDto dto = new UserSignUpRequestServiceDto("username", rawPassword, "slackId");
		when(userHelper.encodePassword(rawPassword, passwordEncoder)).thenReturn(encodedPassword);
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		userService.signUpUser(dto);

		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());
		User savedUser = userCaptor.getValue();
		assertEquals("username", savedUser.getUsername());
		assertEquals(encodedPassword, savedUser.getPassword());
		assertEquals("slackId", savedUser.getSlackId());
	}

	@Test
	@Description(
		"Slack 코드 요청 성공 케이스: " +
			"주어진 사용자 이름으로 유저가 존재하고, 상태가 PENDING이며, " +
			"Slack 코드가 생성되고 저장소에 저장되며, 이전 코드가 없는 경우 " +
			"정상적으로 Slack 메시지가 전송되고 코드가 저장되는지 검증"
	)
	public void slackCodeRequestUser_success() {
		UserSlackCodeRequestServiceDto dto = new UserSlackCodeRequestServiceDto("testUser");
		User user = User.builder()
				.id(1L)
				.username("testUser")
				.slackId("slack123")
				.status(UserConfirmStatus.PENDING)
				.build();
		when(userHelper.findUserByUsername("testUser", userRepository)).thenReturn(user);
		when(directMessageAuthService.generateCode()).thenReturn("slackCode123");
		when(directMessageAuthService.makeDirectMessage("slackCode123")).thenReturn("Your verification code is: slackCode123");
		when(codeStorage.hasKey("testUser")).thenReturn(false);

		userService.slackCodeRequestUser(dto);

		verify(userMessageService).sendSlackMessage(1L, "Your verification code is: slackCode123");
		verify(codeStorage).storeCode("testUser", "slack123", "slackCode123", 300L);
		verify(codeStorage, never()).removeCode(anyString());
	}

	@Test
	@Description(
		"Slack 코드 요청 실패 케이스: " +
			"주어진 사용자 이름으로 유저가 존재하지 않을 때 " +
			"UserException(NOT_FOUND)이 발생하며, " +
			"Slack 코드 생성 및 이후 로직이 실행되지 않는지 검증"
	)
	public void slackCodeRequestUser_userNotFound_throwsException() {
		// Given
		UserSlackCodeRequestServiceDto dto = new UserSlackCodeRequestServiceDto("testUser");
		when(userHelper.findUserByUsername("testUser", userRepository))
				.thenThrow(new UserException(ErrorCode.NOT_FOUND));

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> userService.slackCodeRequestUser(dto));
		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		verify(directMessageAuthService, never()).generateCode();
	}

	@Test
	@Description(
		"Slack 코드 요청 실패 케이스: " +
			"유저의 username이 null일 때 " +
			"UserException(USERNAME_NULL)이 발생하며, " +
			"Slack 코드 생성 및 이후 로직이 실행되지 않는지 검증"
	)
	public void slackCodeRequestUser_usernameNull_throwsException() {
		UserSlackCodeRequestServiceDto dto = new UserSlackCodeRequestServiceDto("testUser");
		User user = User.builder()
				.id(1L)
				.username(null)
				.slackId("slack123")
				.status(UserConfirmStatus.PENDING)
				.build();
		when(userHelper.findUserByUsername("testUser", userRepository))
				.thenThrow(new UserException(ErrorCode.USERNAME_NULL));
		doThrow(new UserException(ErrorCode.USERNAME_NULL)).when(userHelper).validateRequiredField(null, ErrorCode.USERNAME_NULL);

		UserException exception = assertThrows(UserException.class, () -> userService.slackCodeRequestUser(dto));
		assertEquals(ErrorCode.USERNAME_NULL, exception.getErrorCode());
		verify(directMessageAuthService, never()).generateCode();
	}

	@Test
	@Description(
		"Slack 코드 요청 실패 케이스: " +
			"유저의 slackId가 null일 때 " +
			"UserException(SLACKID_NULL)이 발생하며, " +
			"Slack 코드 생성 및 이후 로직이 실행되지 않는지 검증"
	)
	public void slackCodeRequestUser_slackIdNull_throwsException() {
		UserSlackCodeRequestServiceDto dto = new UserSlackCodeRequestServiceDto("testUser");
		User user = User.builder()
				.id(1L)
				.username("testUser")
				.slackId(null)
				.status(UserConfirmStatus.PENDING)
				.build();
		when(userHelper.findUserByUsername("testUser", userRepository))
				.thenThrow(new UserException(ErrorCode.SLACKID_NULL));
		doThrow(new UserException(ErrorCode.SLACKID_NULL)).when(userHelper).validateRequiredField(null, ErrorCode.SLACKID_NULL);

		UserException exception = assertThrows(UserException.class, () -> userService.slackCodeRequestUser(dto));
		assertEquals(ErrorCode.SLACKID_NULL, exception.getErrorCode());
		verify(directMessageAuthService, never()).generateCode();
	}

	@Test
	@Description(
		"Slack 코드 요청 실패 케이스: " +
			"유저의 상태가 CONFIRMED일 때 " +
			"UserException(SLACK_ALREADY_AUTH)이 발생하며, " +
			"Slack 코드 생성 및 이후 로직이 실행되지 않는지 검증"
	)
	public void slackCodeRequestUser_alreadyConfirmed_throwsException() {
		// Given
		UserSlackCodeRequestServiceDto dto = new UserSlackCodeRequestServiceDto("testUser");
		User user = User.builder()
				.id(1L)
				.username("testUser")
				.slackId("slack123")
				.status(UserConfirmStatus.CONFIRMED)
				.build();
		when(userHelper.findUserByUsername("testUser", userRepository)).thenReturn(user);

		// When & Then
		UserException exception = assertThrows(UserException.class, () -> userService.slackCodeRequestUser(dto));
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
	public void slackCodeRequestUser_existingCode_removesAndStores() {
		UserSlackCodeRequestServiceDto dto = new UserSlackCodeRequestServiceDto("testUser");
		User user = User.builder()
				.id(1L)
				.username("testUser")
				.slackId("slack123")
				.status(UserConfirmStatus.PENDING)
				.build();
		when(userHelper.findUserByUsername("testUser", userRepository)).thenReturn(user);
		when(directMessageAuthService.generateCode()).thenReturn("slackCode123");
		when(directMessageAuthService.makeDirectMessage("slackCode123")).thenReturn("Your verification code is: slackCode123");
		when(codeStorage.hasKey("testUser")).thenReturn(true);

		userService.slackCodeRequestUser(dto);

		verify(codeStorage).removeCode("testUser");
		verify(userMessageService).sendSlackMessage(1L, "Your verification code is: slackCode123");
		verify(codeStorage).storeCode("testUser", "slack123", "slackCode123", 300L);
	}
}

