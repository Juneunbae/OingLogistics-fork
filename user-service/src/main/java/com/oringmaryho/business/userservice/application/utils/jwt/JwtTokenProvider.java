package com.oringmaryho.business.userservice.application.utils.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	public String generateAccessToken(Long userId) {
		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
			.signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
			.compact();
	}

	public String generateRefreshToken(Long userId) {
		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
			.signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
			.compact();
	}

	public long getRefreshTokenExpiration() {
		return refreshTokenExpiration;
	}


	// JWT 토큰 파싱
	public Claims parseJwtToken(String token) {
		try {
			return Jwts.parser()
				.setSigningKey(Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8)))
				.parseClaimsJws(token)
				.getBody();
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid JWT token");
		}
	}

	// 남은 TTL 계산
	public long calculateTtlMillis(Date expiration) {
		long currentTimeMillis = System.currentTimeMillis();
		long expirationTimeMillis = expiration.getTime();
		return Math.max(0, expirationTimeMillis - currentTimeMillis); // 음수가 되지 않도록
	}
}
