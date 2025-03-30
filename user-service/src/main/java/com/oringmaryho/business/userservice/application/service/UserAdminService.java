package com.oringmaryho.business.userservice.application.service;

import com.oringmaryho.business.userservice.application.UserHelper;
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
  private final UserHelper userHelper;

  @Value("${admin.key}")
  private String adminKey;
  @Value("${slack.code.ttl}")
  private Long SLACK_CODE_TTL;

  @Transactional
  public void signUpUserAdmin(UserAdminSignUpRequestServiceDto requestServiceDto) {
    userHelper.validateRequiredField(requestServiceDto.username(), ErrorCode.USERNAME_NULL);
    userHelper.validateRequiredField(requestServiceDto.password(), ErrorCode.PASSWORD_NULL);
    userHelper.validateRequiredField(requestServiceDto.slackId(), ErrorCode.SLACKID_NULL);
    userHelper.validateRequiredField(requestServiceDto.key(), ErrorCode.ADMIN_REGISTER_KEY_IS_NULL);

    userHelper.usernameVerify(requestServiceDto.username());
    userHelper.passwordVerify(requestServiceDto.password());
    userHelper.checkUsernameExists(requestServiceDto.username(), userRepository);

    if (!requestServiceDto.key().equals(adminKey)) {
      throw new UserException(ErrorCode.ADMIN_REGISTER_KEY_NOT_MATCH);
    }

    String encodedPassword = userHelper.encodePassword(requestServiceDto.password(),
        passwordEncoder);
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
    userHelper.validateRequiredField(requestServiceDto.username(), ErrorCode.USERNAME_NULL);
    userHelper.validateRequiredField(requestServiceDto.password(), ErrorCode.PASSWORD_NULL);
    userHelper.validateRequiredField(requestServiceDto.slackId(), ErrorCode.SLACKID_NULL);

    userHelper.usernameVerify(requestServiceDto.username());
    userHelper.passwordVerify(requestServiceDto.password());
    userHelper.checkUsernameExists(requestServiceDto.username(), userRepository);

    String encodedPassword = userHelper.encodePassword(requestServiceDto.password(),
        passwordEncoder);
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
    String slackCode = codeStorage.getCode(username);

    if (slackCode != null
        && codeStorage.getSlackUsername(username).equals(slackId)
        && slackCode.equals(requestServiceDto.confirmCode())) {
      User user = userHelper.findUserByUsername(username, userRepository);
      user.changeStatus(UserConfirmStatus.CONFIRMED);
      codeStorage.removeCode(username);
    } else {
      throw new UserException(ErrorCode.SLACK_AUTH_FAIL);
    }

    User updatedUser = userHelper.findUserByUsername(username, userRepository);
    userHelper.updateRedisUserInfo(updatedUser, redisUtil);
  }

  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "user", key = "#requestServiceDto.id()")
  public UserAdminFindResponseDto findUserAdmin(UserAdminFindRequestServiceDto requestServiceDto) {
    User user = userHelper.findUserById(requestServiceDto.id(), userRepository);
    return userApplicationMapper.toUserAdminFindResponseDto(user);
  }

  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "users")
  public Page<UserAdminSearchResponseDto> searchUsers(
      UserAdminSearchRequestServiceDto requestServiceDto, Pageable pageable) {
    Page<User> users = customUserRepository.findDynamicQuery(
        createUserSearchCriteria(requestServiceDto),
        pageable);
    return users.map(userApplicationMapper::toUserAdminSearchResponseDto);
  }

  @Transactional
  public UserAdminUpdateResponseDto updateUser(UserAdminUpdateRequestServiceDto requestServiceDto) {
    if (requestServiceDto.id() == null || requestServiceDto.id() < 1) {
      throw new UserException(ErrorCode.USERNAME_NULL);
    }

    User user = userHelper.findUserById(requestServiceDto.id(), userRepository);

    if (requestServiceDto.username() != null && !requestServiceDto.username().isEmpty()) {
      userHelper.usernameVerify(requestServiceDto.username());
      user.updateUsername(requestServiceDto.username());
    }
    if (requestServiceDto.password() != null && !requestServiceDto.password().isEmpty()) {
      userHelper.passwordVerify(requestServiceDto.password());
      String encodedPassword = userHelper.encodePassword(requestServiceDto.password(),
          passwordEncoder);
      user.updatePassword(encodedPassword);
    }
    if (requestServiceDto.slackId() != null && !requestServiceDto.slackId().isEmpty()) {
      user.updateSlackId(requestServiceDto.slackId());
    }

    User updatedUser = userHelper.findUserById(requestServiceDto.id(), userRepository);
    userHelper.updateRedisUserInfo(updatedUser, redisUtil);
    return userApplicationMapper.toUserAdminUpdateResponseDto(user.getId());
  }

  @Transactional
  public UserAdminGrantRoleResponseDto grantRoleUser(
      UserAdminGrantRoleRequestServiceDto requestServiceDto) {
    if (requestServiceDto.role().equals(UserRoleType.MASTER)) {
      throw new UserException(ErrorCode.CANNOT_GRANT_MASTER_ROLE);
    }

    User user = userHelper.findUserById(requestServiceDto.id(), userRepository);
    user.updateRoleType(requestServiceDto.role());

    User updatedUser = userHelper.findUserById(requestServiceDto.id(), userRepository);
    userHelper.updateRedisUserInfo(updatedUser, redisUtil);
    return userApplicationMapper.toUserAdminGrantRoleResponseDto(requestServiceDto.id());
  }

  @Transactional
  public UserAdminUpdateRoleResponseDto updateRoleUser(
      UserAdminUpdateRoleRequestServiceDto requestServiceDto) {
    User user = userHelper.findUserById(requestServiceDto.id(), userRepository);
    userHelper.checkMasterRole(user);

    UserRoleType role = user.getRole();
    user.updateRoleType(requestServiceDto.newRole());

    User updatedUser = userHelper.findUserById(requestServiceDto.id(), userRepository);
    userHelper.updateRedisUserInfo(updatedUser, redisUtil);
    return userApplicationMapper.toUserAdminUpdateRoleResponseDto(
        requestServiceDto.id(), role, requestServiceDto.newRole());
  }

  @Transactional
  public void deleteRoleUser(UserAdminDeleteRoleRequestServiceDto requestServiceDto) {
    User user = userHelper.findUserById(requestServiceDto.id(), userRepository);
    userHelper.checkMasterRole(user);

    user.deleteRoleType();

    User updatedUser = userHelper.findUserById(requestServiceDto.id(), userRepository);
    userHelper.updateRedisUserInfo(updatedUser, redisUtil);
  }

  @Transactional
  public void deleteUser(UserAdminDeleteRequestServiceDto requestServiceDto) {
    User user = userHelper.findUserById(requestServiceDto.id(), userRepository);
    user.softDelete(user.getId());
    messagePublisher.publishUserStatus(user.getId());

    String userInfoKey = "user:info:" + user.getId();
    String tokenKey = "user:token:" + user.getId();
    redisTemplate.delete(userInfoKey);
    redisTemplate.delete(tokenKey);
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

    Claims claims = jwtTokenProvider.parseJwtToken(accessToken);
    long ttlMillis = jwtTokenProvider.calculateTtlMillis(claims.getExpiration());

    String blacklistKey = "blacklist:" + accessToken;
    redisTemplate.opsForValue().set(blacklistKey, "blacklisted");

    if (ttlMillis > 0) {
      redisTemplate.expire(blacklistKey, ttlMillis, TimeUnit.MILLISECONDS);
    } else {
      redisTemplate.expire(blacklistKey, 1, TimeUnit.SECONDS);
    }

    if (userId != null) {
      String userInfoKey = "user:token:" + userId;
      redisTemplate.delete(userInfoKey);
    }
  }

  public void slackCodeRequestUser(UserAdminSlackCodeRequestServiceDto requestServiceDto) {
    User user = userHelper.findUserByUsername(requestServiceDto.username(), userRepository);
    userHelper.validateRequiredField(user.getUsername(), ErrorCode.USERNAME_NULL);
    userHelper.validateRequiredField(user.getSlackId(), ErrorCode.SLACKID_NULL);

    if (user.getStatus().equals(UserConfirmStatus.CONFIRMED)) {
      throw new UserException(ErrorCode.SLACK_ALREADY_AUTH);
    }

    String slackCode = directMessageAuthService.generateCode();
    String slackMessage = directMessageAuthService.makeDirectMessage(slackCode);
    userMessageService.sendSlackMessage(user.getId(), slackMessage);

    if (codeStorage.hasKey(user.getUsername())) {
      codeStorage.removeCode(user.getUsername());
    }
    codeStorage.storeCode(user.getUsername(), user.getSlackId(), slackCode, SLACK_CODE_TTL);
  }

  public UserSignInResponseDto signInUserAdmin(UserAdminSignInRequestServiceDto requestServiceDto) {
    if (!userRepository.existsByUsername(requestServiceDto.username())) {
      throw new UserException(ErrorCode.NOT_FOUND);
    }

    User user = userHelper.findUserByUsername(requestServiceDto.username(), userRepository);
    userHelper.updateRedisUserInfo(user, redisUtil);
    Map<String, String> tokenMap = redisUtil.updateUserJwtToken(user.getId());

    UserSignInResponseServiceDto serviceDto = new UserSignInResponseServiceDto(
        tokenMap.get("accessToken"),
        tokenMap.get("refreshToken")
    );
    return userApplicationMapper.toSignInResponseDto(serviceDto);
  }
}