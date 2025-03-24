package com.oringmaryho.business.userservice.application.service;

import com.oringmaryho.business.userservice.application.dto.mapper.UserApplicationMapper;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminCreateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminDeleteRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminDeleteRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminFindRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminGrantRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSignInRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSlackCodeRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminUpdateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserAdminUpdateRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignOutRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserAdminFindResponseDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSignInResponseServiceDto;
import com.oringmaryho.business.userservice.application.messaging.MessagePublisher;
import com.oringmaryho.business.userservice.application.messaging.UserMessageService;
import com.oringmaryho.business.userservice.application.utils.CodeStorage;
import com.oringmaryho.business.userservice.application.utils.DirectMessageAuthService;
import com.oringmaryho.business.userservice.application.utils.RedisUtil;
import com.oringmaryho.business.userservice.application.utils.jwt.JwtTokenProvider;
import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.UserConfirmStatus;
import com.oringmaryho.business.userservice.domain.UserRoleType;
import com.oringmaryho.business.userservice.domain.UserSearchCriteria;
import com.oringmaryho.business.userservice.domain.repository.CustomUserRepository;
import com.oringmaryho.business.userservice.domain.repository.UserRepository;
import com.oringmaryho.business.userservice.exception.ErrorCode;
import com.oringmaryho.business.userservice.exception.UserException;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminGrantRoleResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminUpdateResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserAdminUpdateRoleResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSignInResponseDto;
import io.jsonwebtoken.Claims;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdminService {

  private static final String USERNAME_REGEX = "^[a-z0-9]{4,10}$";
  private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,15}$";
  private final UserRepository userRepository;
  private final CustomUserRepository customUserRepository;
  private final UserApplicationMapper userApplicationMapper;
  private final PasswordEncoder passwordEncoder;
  private final RedisTemplate<String, Object> redisTemplate;
  private final JwtTokenProvider jwtTokenProvider;
  private final RedisUtil redisUtil;
  private final UserMessageService userMessageService;
  private final DirectMessageAuthService directMessageAuthService;
  private final CodeStorage codeStorage;
  private final MessagePublisher messagePublisher;

  @Value("${admin.key}")
  private String adminKey;
  @Value("${slack.code.ttl}")
  private Long SLACK_CODE_TTL;

  @Transactional
  public void signUpUserAdmin(UserAdminSignUpRequestServiceDto requestServiceDto) {
    //null처리
    validateRequiredField(requestServiceDto.username(), ErrorCode.USERNAME_NULL);
    validateRequiredField(requestServiceDto.password(), ErrorCode.PASSWORD_NULL);
    validateRequiredField(requestServiceDto.slackId(), ErrorCode.SLACKID_NULL);
    validateRequiredField(requestServiceDto.key(), ErrorCode.ADMIN_REGISTER_KEY_IS_NULL);

    //형식에 맞는지 체크
    usernameVerify(requestServiceDto.username());
    passwordVerify(requestServiceDto.password());

    // username 중복 체크
    if (userRepository.existsByUsername(requestServiceDto.username())) {
      throw new UserException(ErrorCode.ALREADY_EXISTS);
    }

    //key 검증
    if (!requestServiceDto.key().equals(adminKey)) {
      throw new UserException(ErrorCode.ADMIN_REGISTER_KEY_NOT_MATCH);
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
    //null처리
    validateRequiredField(requestServiceDto.username(), ErrorCode.USERNAME_NULL);
    validateRequiredField(requestServiceDto.password(), ErrorCode.PASSWORD_NULL);
    validateRequiredField(requestServiceDto.slackId(), ErrorCode.SLACKID_NULL);

    //형식에 맞는지 체크
    usernameVerify(requestServiceDto.username());
    passwordVerify(requestServiceDto.password());

    // username 중복 체크
    if (userRepository.existsByUsername(requestServiceDto.username())) {
      throw new UserException(ErrorCode.ALREADY_EXISTS);
    }

    // 비번 암호화
    String encodedPassword = passwordEncoder.encode(requestServiceDto.password());

    // DTO -> Entity 변환 후 저장
    User user = User.builder()
        .username(requestServiceDto.username())
        .password(encodedPassword)
        .slackId(requestServiceDto.slackId())
        .build();

    userRepository.save(user);
  }

  @Transactional
  public void slackConfirmUser(UserAdminSlackConfirmRequestServiceDto requestServiceDto) {
    String username = requestServiceDto.username();
    String slackId = requestServiceDto.slackId();

    String slackCode = codeStorage.getCode(requestServiceDto.username());
    log.info("slack code is {}", slackCode);

    if (slackCode != null
        && codeStorage.getSlackUsername(username).equals(slackId)
        && slackCode.equals(requestServiceDto.confirmCode())
    ) {
      User user = userRepository.findByUsername(username)
          .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));
      user.changeStatus(UserConfirmStatus.CONFIRMED);

      codeStorage.removeCode(requestServiceDto.username());
    } else {
      throw new UserException(ErrorCode.SLACK_AUTH_FAIL);
    }

    User updatedUser = userRepository.findByUsername(requestServiceDto.username())
        .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));

    redisUtil.updateUserInfo(updatedUser);
  }

  //유저 단일 조회 메서드
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "user", key = "#requestServiceDto.id()")
  public UserAdminFindResponseDto findUserAdmin(UserAdminFindRequestServiceDto requestServiceDto) {
    User user = userRepository.findById(requestServiceDto.id())
        .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));

    return userApplicationMapper.toUserAdminFindResponseDto(user);
  }

  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "users")
  public Page<UserAdminSearchResponseDto> searchUsers(
      UserAdminSearchRequestServiceDto requestServiceDto, Pageable pageable) {

    //쿼리 dsl로 유저 조회
    Page<User> users = customUserRepository.findDynamicQuery(
        createUserSearchCriteria(requestServiceDto),
        pageable);

    return users.map(userApplicationMapper::toUserAdminSearchResponseDto);
  }

  @Transactional
  public UserAdminUpdateResponseDto updateUser(UserAdminUpdateRequestServiceDto requestServiceDto) {
    if (requestServiceDto.id() == null && requestServiceDto.id() < 1) {
      throw new UserException(ErrorCode.USERNAME_NULL);
    }

    User user = userRepository.findById(requestServiceDto.id())
        .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));

    //db에 업데이트
    if (requestServiceDto.username() != null && !requestServiceDto.username().isEmpty()) {
      //username 형식에 맞는지 체크
      usernameVerify(requestServiceDto.username());
      user.updateUsername(requestServiceDto.username());
    }
    if (requestServiceDto.password() != null && !requestServiceDto.password().isEmpty()) {
      //비밀번호 형식에 맞는지 체크
      passwordVerify(requestServiceDto.password());
      String encodedPassword = passwordEncoder.encode(requestServiceDto.password());
      user.updatePassword(encodedPassword);
    }
    if (requestServiceDto.slackId() != null && !requestServiceDto.slackId().isEmpty()) {
      user.updateSlackId(requestServiceDto.slackId());
    }

    User updatedUser = userRepository.findById(requestServiceDto.id())
        .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));

    //redis에 업데이트
    redisUtil.updateUserInfo(updatedUser);

    return userApplicationMapper.toUserAdminUpdateResponseDto(user.getId());
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

    User updatedUser = userRepository.findById(requestServiceDto.id())
        .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));

    //redis에 업데이트
    redisUtil.updateUserInfo(updatedUser);

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

    if (!user.getRole().equals(UserRoleType.MASTER)) {
      throw new UserException(ErrorCode.LESS_ROLE);
    }

    UserRoleType role = user.getRole();

    //db에 업데이트
    user.updateRoleType(newRole);

    User updatedUser = userRepository.findById(requestServiceDto.id())
        .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));

    //redis에 업데이트
    redisUtil.updateUserInfo(updatedUser);

    UserAdminUpdateRoleResponseDto responseDto = userApplicationMapper.toUserAdminUpdateRoleResponseDto(
        curUserId, role, newRole);
    return responseDto;
  }

  @Transactional
  public void deleteRoleUser(UserAdminDeleteRoleRequestServiceDto requestServiceDto) {

    Long userId = requestServiceDto.id();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));

    if (!user.getRole().equals(UserRoleType.MASTER)) {
      throw new UserException(ErrorCode.LESS_ROLE);
    }

    user.deleteRoleType();

    User updatedUser = userRepository.findById(requestServiceDto.id())
        .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));

    //redis에 업데이트
    redisUtil.updateUserInfo(updatedUser);
  }

  @Transactional
  public void deleteUser(UserAdminDeleteRequestServiceDto requestServiceDto) {

    User user = userRepository.findById(requestServiceDto.id())
        .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));

    user.softDelete(user.getId());

    messagePublisher.publishUserStatus(user.getId());
  }

  //username 형식에 맞는지 체크
  public void usernameVerify(String username) {
    if (!Pattern.matches(USERNAME_REGEX, username)) {
      throw new UserException(ErrorCode.USERNAME_REGEX_NOT_MATCH);
    }
  }

  //password 형식에 맞는지 체크
  public void passwordVerify(String password) {
    if (!Pattern.matches(PASSWORD_REGEX, password)) {
      throw new UserException(ErrorCode.PASSWORD_REGEX_NOT_MATCH);
    }
  }

  //null 체크
  private void validateRequiredField(String value, ErrorCode errorCode) {
    if (value == null || value.isEmpty()) {
      throw new UserException(errorCode);
    }
  }

  public UserSearchCriteria createUserSearchCriteria(UserAdminSearchRequestServiceDto requestDto) {
    return UserSearchCriteria.builder()
        .id(requestDto.id())
        .username(requestDto.username())
        .slackId(requestDto.slackId())
        .role(requestDto.role())
        .status(requestDto.status())
        .isDeleted(requestDto.isDeleted())
        .build();
  }

  public void signOutUser(UserSignOutRequestServiceDto requestServiceDto) {
    Long userId = requestServiceDto.id();
    String tokenKey = "user:token:" + userId;
    if (!redisTemplate.hasKey(tokenKey)) {
      throw new UserException(ErrorCode.NOT_FOUND);
    }
    Map<Object, Object> token = redisTemplate.opsForHash().entries(tokenKey);
    String accessToken = (String) token.get("accessToken");

    if (token == null || token.isEmpty()) {
      throw new UserException(ErrorCode.JWT_REQUIRED);
    }

    // JWT에서 만료 시간 추출
    Claims claims = jwtTokenProvider.parseJwtToken(accessToken);
    long ttlMillis = jwtTokenProvider.calculateTtlMillis(claims.getExpiration());

    // 블랙리스트에 토큰 추가
    String blacklistKey = "blacklist:" + accessToken;
    redisTemplate.opsForValue().set(blacklistKey, "blacklisted");

    // JWT 만료 시간에 맞춰 TTL 설정
    if (ttlMillis > 0) {
      redisTemplate.expire(blacklistKey, ttlMillis, TimeUnit.MILLISECONDS);
      log.info("블랙리스트에 추가된 토큰 : {} TTL: {} ms", token, ttlMillis);
    } else {
      // 만료 시간이 이미 지난 경우, 최소 TTL 설정
      redisTemplate.expire(blacklistKey, 1, TimeUnit.SECONDS);
      log.warn("토큰 {} 은 이미 만료되어 최소 TTL로 설정", token);
    }

    // 사용자 토큰 정보 정리
    if (userId != null) {
      String userInfoKey = "user:token:" + userId;
      redisTemplate.delete(userInfoKey);
    }
  }

  public void slackCodeRequestUser(UserAdminSlackCodeRequestServiceDto requestServiceDto) {
    User user = userRepository.findByUsername(requestServiceDto.username())
        .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));

    validateRequiredField(user.getUsername(), ErrorCode.USERNAME_NULL);
    validateRequiredField(user.getSlackId(), ErrorCode.SLACKID_NULL);

    if (!userRepository.existsByUsername(requestServiceDto.username())) {
      throw new UserException(ErrorCode.NOT_FOUND);
    }

    if (user.getStatus().equals(UserConfirmStatus.CONFIRMED)) {
      throw new UserException(ErrorCode.SLACK_ALREADY_AUTH);
    }

    //코드 생성
    String slackCode = directMessageAuthService.generateCode();
    //메시지 생성
    String slackMessage = directMessageAuthService.makeDirectMessage(slackCode);
    userMessageService.sendSlackMessage(user.getId(), slackMessage);

    //todo: 차후 사용자 경험을 위해 슬랙 서비스에서 발송 성공 시 성공 메시지 받아 실행하도록 수정
    //이전에 요청한 적 있는 유저 id라면 스토리지에 있는 내용 삭제 후 다시 저장
    if (codeStorage.hasKey(user.getUsername())) {
      codeStorage.removeCode(user.getUsername());
    }
    codeStorage.storeCode(user.getUsername(), user.getSlackId(), slackCode,
        SLACK_CODE_TTL);
  }

  public UserSignInResponseDto signInUserAdmin(UserAdminSignInRequestServiceDto requestServiceDto) {
    // 사용자 인증
    if (!userRepository.existsByUsername(requestServiceDto.username())) {
      throw new UserException(ErrorCode.NOT_FOUND);
    }

    // 인증된 사용자 정보 가져오기
    User user = userRepository.findByUsername(requestServiceDto.username()).orElseThrow(
        () -> new UserException(ErrorCode.NOT_FOUND)
    );

    redisUtil.updateUserInfo(user);
    Map<String, String> tokenMap = redisUtil.updateUserJwtToken(user.getId());

    // 응답 DTO 생성
    UserSignInResponseServiceDto serviceDto = new UserSignInResponseServiceDto(
        tokenMap.get("accessToken"),
        tokenMap.get("refreshToken")
    );

    return userApplicationMapper.toSignInResponseDto(serviceDto);
  }
}
