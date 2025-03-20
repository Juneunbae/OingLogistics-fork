package com.oringmaryho.business.userservice.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.oringmaryho.business.userservice.application.dto.request.UserSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.service.UserService;
import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.repository.UserRepository;

public class UserServiceTest {

	@InjectMocks
	private UserService userService;  // 테스트 대상 클래스

	@Mock
	private UserRepository userRepository;  // 의존성 Mock

	@Mock
	private PasswordEncoder passwordEncoder;  // 의존성 Mock

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);  // Mock 객체 초기화
	}

	@Test
	// username이 null일 경우 테스트
	public void testSignUpUser_whenUsernameIsNull_shouldThrowException() {
		// Given
		UserSignUpRequestServiceDto requestServiceDto = new UserSignUpRequestServiceDto(null, "password123", "slackId");

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			userService.signUpUser(requestServiceDto);
		});
		assertEquals("사용자 이름은 비어 있을 수 없습니다.", exception.getMessage());
	}

	@Test
	// password가 null일 경우 테스트
	public void testSignUpUser_whenPasswordIsNull_shouldThrowException() {
		// Given
		UserSignUpRequestServiceDto requestServiceDto = new UserSignUpRequestServiceDto("username", null, "slackId");

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			userService.signUpUser(requestServiceDto);
		});
		assertEquals("비밀번호는 비어 있을 수 없습니다.", exception.getMessage());
	}

	@Test
	// slackId가 null일 경우 테스트
	public void testSignUpUser_whenSlackIdIsNull_shouldThrowException() {
		// Given
		UserSignUpRequestServiceDto requestServiceDto = new UserSignUpRequestServiceDto("username", "password", null);

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			userService.signUpUser(requestServiceDto);
		});
		assertEquals("slackId는 비어 있을 수 없습니다.", exception.getMessage());
	}

	@Test
	// password 인코딩 테스트
	public void testSignUpUser_passwordEncoding() {
		// Given
		String rawPassword = "password123";
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
}

