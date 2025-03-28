package com.oringmaryho.business.userservice.application;

import com.oringmaryho.business.userservice.application.dto.request.UserAdminCreateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.service.UserAdminService;
import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.UserRoleType;
import com.oringmaryho.business.userservice.domain.repository.UserRepository;
import com.oringmaryho.business.userservice.exception.ErrorCode;
import com.oringmaryho.business.userservice.exception.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserAdminServiceTest {

	@InjectMocks
	private UserAdminService userAdminService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserHelper userHelper;

	@Mock
	private PasswordEncoder passwordEncoder;

	private final String adminKey = "admin-key123";

	@BeforeEach
	void setUp() throws NoSuchFieldException, IllegalAccessException {
		MockitoAnnotations.openMocks(this);
		// Inject adminKey using reflection since it's a @Value field
		Field adminKeyField = UserAdminService.class.getDeclaredField("adminKey");
		adminKeyField.setAccessible(true);
		adminKeyField.set(userAdminService, adminKey);
	}

	// Tests for signUpUserAdmin
	@Test
	void signUpUserAdmin_whenUsernameIsNull_shouldThrowException() {
		UserAdminSignUpRequestServiceDto dto = new UserAdminSignUpRequestServiceDto(null, "password123", "slackId", adminKey);
		doThrow(new UserException(ErrorCode.USERNAME_NULL)).when(userHelper).validateRequiredField(null, ErrorCode.USERNAME_NULL);

		UserException exception = assertThrows(UserException.class, () -> userAdminService.signUpUserAdmin(dto));
		assertEquals(ErrorCode.USERNAME_NULL, exception.getErrorCode());
	}

	@Test
	void signUpUserAdmin_whenPasswordIsNull_shouldThrowException() {
		UserAdminSignUpRequestServiceDto dto = new UserAdminSignUpRequestServiceDto("username", null, "slackId", adminKey);
		doThrow(new UserException(ErrorCode.PASSWORD_NULL)).when(userHelper).validateRequiredField(null, ErrorCode.PASSWORD_NULL);

		UserException exception = assertThrows(UserException.class, () -> userAdminService.signUpUserAdmin(dto));
		assertEquals(ErrorCode.PASSWORD_NULL, exception.getErrorCode());
	}

	@Test
	void signUpUserAdmin_whenSlackIdIsNull_shouldThrowException() {
		UserAdminSignUpRequestServiceDto dto = new UserAdminSignUpRequestServiceDto("username", "password123", null, adminKey);
		doThrow(new UserException(ErrorCode.SLACKID_NULL)).when(userHelper).validateRequiredField(null, ErrorCode.SLACKID_NULL);

		UserException exception = assertThrows(UserException.class, () -> userAdminService.signUpUserAdmin(dto));
		assertEquals(ErrorCode.SLACKID_NULL, exception.getErrorCode());
	}

	@Test
	void signUpUserAdmin_whenKeyIsNull_shouldThrowException() {
		UserAdminSignUpRequestServiceDto dto = new UserAdminSignUpRequestServiceDto("username", "password123", "slackId", null);
		doThrow(new UserException(ErrorCode.ADMIN_REGISTER_KEY_IS_NULL)).when(userHelper).validateRequiredField(null, ErrorCode.ADMIN_REGISTER_KEY_IS_NULL);

		UserException exception = assertThrows(UserException.class, () -> userAdminService.signUpUserAdmin(dto));
		assertEquals(ErrorCode.ADMIN_REGISTER_KEY_IS_NULL, exception.getErrorCode());
	}

	@Test
	void signUpUserAdmin_whenUsernameAlreadyExists_shouldThrowException() {
		UserAdminSignUpRequestServiceDto dto = new UserAdminSignUpRequestServiceDto("user01", "password1!", "slackId", adminKey);
		doThrow(new UserException(ErrorCode.ALREADY_EXISTS)).when(userHelper).checkUsernameExists("user01", userRepository);

		UserException exception = assertThrows(UserException.class, () -> userAdminService.signUpUserAdmin(dto));
		assertEquals(ErrorCode.ALREADY_EXISTS, exception.getErrorCode());
	}

	@Test
	void signUpUserAdmin_whenKeyDoesNotMatch_shouldThrowException() {
		UserAdminSignUpRequestServiceDto dto = new UserAdminSignUpRequestServiceDto("username", "password1!", "slackId", "wrongAdminKey");

		UserException exception = assertThrows(UserException.class, () -> userAdminService.signUpUserAdmin(dto));
		assertEquals(ErrorCode.ADMIN_REGISTER_KEY_NOT_MATCH, exception.getErrorCode());
	}

	@Test
	void signUpUserAdmin_success_savesUserWithMasterRole() {
		UserAdminSignUpRequestServiceDto dto = new UserAdminSignUpRequestServiceDto("testuser1", "Password1!@#", "slack123", adminKey);
		when(userHelper.encodePassword("Password1!@#", passwordEncoder)).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		userAdminService.signUpUserAdmin(dto);

		verify(userRepository).save(argThat(user ->
				user.getUsername().equals("testuser1") &&
						user.getPassword().equals("encodedPassword") &&
						user.getSlackId().equals("slack123") &&
						user.getRole() == UserRoleType.MASTER
		));
	}

	@Test
	void createUser_success_savesUser() {
		// Given
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto("testuser1", "Password1!@#", "slack123");
		when(userHelper.encodePassword("Password1!@#", passwordEncoder)).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		doNothing().when(userHelper).validateRequiredField("testuser1", ErrorCode.USERNAME_NULL);
		doNothing().when(userHelper).validateRequiredField("Password1!@#", ErrorCode.PASSWORD_NULL);
		doNothing().when(userHelper).validateRequiredField("slack123", ErrorCode.SLACKID_NULL);
		doNothing().when(userHelper).usernameVerify("testuser1");
		doNothing().when(userHelper).passwordVerify("Password1!@#");
		doNothing().when(userHelper).checkUsernameExists("testuser1", userRepository);

		// When
		userAdminService.createUser(dto);

		// Then
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());
		User savedUser = userCaptor.getValue();

		assertEquals("testuser1", savedUser.getUsername());
		assertEquals("encodedPassword", savedUser.getPassword());
		assertEquals("slack123", savedUser.getSlackId());
		assertEquals(UserRoleType.DEFAULT, savedUser.getRole());
	}

	@Test
	void createUser_success_savesUser_withStoredState() {
		// Given
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto("testuser1", "Password1!@#", "slack123");
		when(userHelper.encodePassword("Password1!@#", passwordEncoder)).thenReturn("encodedPassword");
		// 저장 후 객체에 ID가 추가된 상태를 시뮬레이션
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User user = invocation.getArgument(0); // 전달된 객체 가져오기
			// 저장 후 상태 반영: 예를 들어 ID 추가
			try {
				Field idField = User.class.getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(user, 1L); // ID 설정
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException("Failed to set ID", e);
			}
			return user; // 수정된 객체 반환
		});
		doNothing().when(userHelper).validateRequiredField(anyString(), any());
		doNothing().when(userHelper).usernameVerify("testuser1");
		doNothing().when(userHelper).passwordVerify("Password1!@#");
		doNothing().when(userHelper).checkUsernameExists("testuser1", userRepository);

		// When
		userAdminService.createUser(dto);

		// Then
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());
		User savedUser = userCaptor.getValue(); // save에 전달된 객체 (저장 전 상태)

		assertEquals("testuser1", savedUser.getUsername());
		assertEquals("encodedPassword", savedUser.getPassword());
		assertEquals("slack123", savedUser.getSlackId());
		assertEquals(UserRoleType.DEFAULT, savedUser.getRole());

		User storedUser = userRepository.save(savedUser);
		assertEquals(1L, storedUser.getId(), "ID should be set after save");
		assertEquals("testuser1", storedUser.getUsername());
		assertEquals("encodedPassword", storedUser.getPassword());
		assertEquals("slack123", storedUser.getSlackId());
		assertEquals(UserRoleType.DEFAULT, savedUser.getRole());
	}

	@Test
	void createUser_usernameNull_throwsException() {
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto(null, "Password123!@#", "slack123");
		doThrow(new UserException(ErrorCode.USERNAME_NULL)).when(userHelper).validateRequiredField(null, ErrorCode.USERNAME_NULL);

		UserException exception = assertThrows(UserException.class, () -> userAdminService.createUser(dto));
		assertEquals(ErrorCode.USERNAME_NULL, exception.getErrorCode());
	}

	@Test
	void createUser_usernameEmpty_throwsException() {
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto("", "Password123!@#", "slack123");
		doThrow(new UserException(ErrorCode.USERNAME_NULL)).when(userHelper).validateRequiredField("", ErrorCode.USERNAME_NULL);

		UserException exception = assertThrows(UserException.class, () -> userAdminService.createUser(dto));
		assertEquals(ErrorCode.USERNAME_NULL, exception.getErrorCode());
	}

	@Test
	void createUser_passwordNull_throwsException() {
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto("testuser123", null, "slack123");
		doThrow(new UserException(ErrorCode.PASSWORD_NULL)).when(userHelper).validateRequiredField(null, ErrorCode.PASSWORD_NULL);

		UserException exception = assertThrows(UserException.class, () -> userAdminService.createUser(dto));
		assertEquals(ErrorCode.PASSWORD_NULL, exception.getErrorCode());
	}

	@Test
	void createUser_slackIdEmpty_throwsException() {
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto("testuser123", "Password123!@#", "");
		doThrow(new UserException(ErrorCode.SLACKID_NULL)).when(userHelper).validateRequiredField("", ErrorCode.SLACKID_NULL);

		UserException exception = assertThrows(UserException.class, () -> userAdminService.createUser(dto));
		assertEquals(ErrorCode.SLACKID_NULL, exception.getErrorCode());
	}

	@Test
	void createUser_usernameInvalidFormat_throwsException() {
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto("TestUser123", "Password123!@#", "slack123");
		doThrow(new UserException(ErrorCode.USERNAME_REGEX_NOT_MATCH)).when(userHelper).usernameVerify("TestUser123");

		UserException exception = assertThrows(UserException.class, () -> userAdminService.createUser(dto));
		assertEquals(ErrorCode.USERNAME_REGEX_NOT_MATCH, exception.getErrorCode());
	}

	@Test
	void createUser_passwordInvalidFormat_throwsException() {
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto("testuser1", "Password123", "slack123");
		doThrow(new UserException(ErrorCode.PASSWORD_REGEX_NOT_MATCH)).when(userHelper).passwordVerify("Password123");

		UserException exception = assertThrows(UserException.class, () -> userAdminService.createUser(dto));
		assertEquals(ErrorCode.PASSWORD_REGEX_NOT_MATCH, exception.getErrorCode());
	}

	@Test
	void createUser_usernameAlreadyExists_throwsException() {
		UserAdminCreateRequestServiceDto dto = new UserAdminCreateRequestServiceDto("testuser1", "Password1!@#", "slack123");
		doThrow(new UserException(ErrorCode.ALREADY_EXISTS)).when(userHelper).checkUsernameExists("testuser1", userRepository);

		UserException exception = assertThrows(UserException.class, () -> userAdminService.createUser(dto));
		assertEquals(ErrorCode.ALREADY_EXISTS, exception.getErrorCode());
	}
}