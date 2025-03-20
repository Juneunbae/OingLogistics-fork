package com.oringmaryho.business.userservice.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oringmaryho.business.userservice.application.dto.request.UserAdminCreateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminFindRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminGrantRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminUpdateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminUpdateRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.UserRoleType;
import com.oringmaryho.business.userservice.domain.repository.UserRepository;
import com.oringmaryho.business.userservice.exception.ErrorCode;
import com.oringmaryho.business.userservice.exception.UserException;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminDeleteRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminDeleteRoleRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminGrantRoleResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminUpdateResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminUpdateRoleResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAdminService {

	private final UserRepository userRepository;
	private final UserApplicationMapper userApplicationMapper;
	private final PasswordEncoder passwordEncoder;

	@Value("${admin.key}")
	private String adminKey;

	@Transactional
	public void signUpUserAdmin(UserAdminSignUpRequestServiceDto requestServiceDto) {
		//null처리
		if (requestServiceDto.username() == null || requestServiceDto.username().isEmpty()) {
			throw new IllegalArgumentException("사용자 이름은 비어 있을 수 없습니다.");
		}
		if (requestServiceDto.password() == null || requestServiceDto.password().isEmpty()) {
			throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
		}
		if (requestServiceDto.slackId() == null || requestServiceDto.slackId().isEmpty()) {
			throw new IllegalArgumentException("slackId는 비어 있을 수 없습니다.");
		}
		if (requestServiceDto.key() == null || requestServiceDto.key().isEmpty()) {
			throw new IllegalArgumentException("key는 비어 있을 수 없습니다.");
		}

		// username 중복 체크
		if (userRepository.existsByUsername(requestServiceDto.username())) {
			throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
		}

		//key 검증
		if (!requestServiceDto.key().equals(adminKey)) {
			throw new IllegalArgumentException("admin키가 일치하지 않습니다.");
		}

		// 비번 암호화
		String encodedPassword = passwordEncoder.encode(requestServiceDto.password());

		// DTO -> Entity 변환 후 저장
		User user = User.builder()
			.username(requestServiceDto.username())
			.password(encodedPassword)
			.slackId(requestServiceDto.slackId())
			.role(UserRoleType.MASTER)
			.build();

		userRepository.save(user);
	}

	@Transactional
	public void createUser(UserAdminCreateRequestServiceDto requestServiceDto) {

	}

	@Transactional
	public void slackConfirmUser(UserSlackConfirmRequestServiceDto requestServiceDto) {
		//todo: 일반 사용자 service에서 기능 가져와서 추가하기
		//todo: 슬랙인증 요청과 승인 요청
	}

	public void findUserAdmin(UserAdminFindRequestServiceDto requestServiceDto) {

	}


	public List<UserAdminSearchResponseDto> searchUsers(
		UserAdminSearchRequestServiceDto requestServiceDto) {
		return null;
	}

	@Transactional
	public UserAdminUpdateResponseDto updateUser(
		UserAdminUpdateRequestServiceDto requestServiceDto) {
		//todo: responseDto 반환
		Long userId = null;
		UserAdminUpdateResponseDto responseDto = userApplicationMapper.toUserAdminUpdateResponseDto(
			userId);
		return responseDto;
	}

	@Transactional
	public UserAdminGrantRoleResponseDto grantRoleUser(
		UserAdminGrantRoleRequestServiceDto requestServiceDto) {
		//todo: null 처리 통일하기 어노테이션으로
		//todo: null 처리 추가

		if (requestServiceDto.role().equals(UserRoleType.MASTER)) {
			throw new UserException(ErrorCode.CANNOT_GRANT_MASTER_ROLE);
		}

		User user = userRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));

		user.updateRoleType(requestServiceDto.role());

		return userApplicationMapper.toUserAdminGrantRoleResponseDto(
			requestServiceDto.id());
	}

	@Transactional
	public UserAdminUpdateRoleResponseDto updateRoleUser(
		UserAdminUpdateRoleRequestServiceDto requestServiceDto) {

		Long curUserId = requestServiceDto.id();

		UserRoleType newRole = requestServiceDto.newRole();

		User user = userRepository.findById(curUserId)
			.orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));

		if(!user.getRole().equals(UserRoleType.MASTER)) {
			throw new UserException(ErrorCode.LESS_ROLE);
		}

		UserRoleType role = user.getRole();

		user.updateRoleType(newRole);

		UserAdminUpdateRoleResponseDto responseDto = userApplicationMapper.toUserAdminUpdateRoleResponseDto(
			curUserId, role, newRole);
		return responseDto;
	}

	@Transactional
	public void deleteRoleUser(UserAdminDeleteRoleRequestServiceDto requestServiceDto) {

		Long userId = requestServiceDto.id();

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));

		if(!user.getRole().equals(UserRoleType.MASTER)) {
			throw new UserException(ErrorCode.LESS_ROLE);
		}



	}

	@Transactional
	public void deleteUser(UserAdminDeleteRequestServiceDto requestServiceDto) {

	}
}
