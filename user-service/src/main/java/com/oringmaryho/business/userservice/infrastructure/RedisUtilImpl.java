package com.oringmaryho.business.userservice.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.oringmaryho.business.userservice.application.utils.RedisUtil;
import com.oringmaryho.business.userservice.application.utils.jwt.JwtTokenProvider;
import com.oringmaryho.business.userservice.domain.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisUtilImpl implements RedisUtil {
	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void updateUserInfo(User user) {
		String userInfoKey = "user:info:" + user.getId();
		Map<String, Object> userInfoMap = new ConcurrentHashMap<>();

		userInfoMap.put("username", user.getUsername());
		userInfoMap.put("slackId", user.getSlackId());
		userInfoMap.put("role", user.getRole());
		userInfoMap.put("status", user.getStatus());

		long expirationTime = jwtTokenProvider.getRefreshTokenExpiration();
		redisTemplate.delete(userInfoKey);
		redisTemplate.opsForHash().putAll(userInfoKey, userInfoMap);
		redisTemplate.expire(userInfoKey, expirationTime, TimeUnit.MILLISECONDS);
	}

	@Override
	public Map<String, String> updateUserJwtToken(Long id) {
		// JWT 토큰 생성
		String accessToken = jwtTokenProvider.generateAccessToken(id);
		String refreshToken = jwtTokenProvider.generateRefreshToken(id);

		// 토큰 정보 키
		String tokenKey = "user:token:" + id;

		// 토큰 정보 저장
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put("accessToken", accessToken);
		tokenMap.put("refreshToken", refreshToken);
		redisTemplate.delete(tokenKey);
		redisTemplate.opsForHash().putAll(tokenKey, tokenMap);

		long expirationTime = jwtTokenProvider.getRefreshTokenExpiration();
		redisTemplate.expire(tokenKey, expirationTime, TimeUnit.MILLISECONDS);

		return tokenMap;
	}


}
