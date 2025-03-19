package com.oingmaryho.infrastructure.gateway_service.filter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

	// 인증 예외 처리할 경로 목록
	private static final List<String> EXCLUDED_PATHS = Arrays.asList(
		"/api/v1/users/sign-up",
		"/admin/v1/users/sign-up",
		"/api/v1/users/sign-in");

	@Value("${spring.cloud.gateway.secrets.secretKey}")
	private String SECRET_KEY;

	private final RedisTemplate<String, Object> redisTemplate;

	public JwtAuthFilter(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange,
		org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getURI().getPath();

		// 인증이 필요 없는 경로는 예외 처리
		if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
			return chain.filter(exchange);
		}

		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.warn("JWT 토큰이 없습니다.");
			return onError(exchange, "JWT 토큰이 필요합니다.", HttpStatus.UNAUTHORIZED);
		}

		String token = authHeader.substring(7);

		try {
			// JWT 검증
			Claims claims = validateToken(token);
			log.info("JWT 검증 성공: {}", claims);

			// 블랙리스트에 있는지 확인
			if (isTokenBlacklisted(token)) {
				log.error("JWT가 블랙리스트에 존재합니다.");
				//todo: gateway 커스텀 에러 적용하기
				return onError(exchange, "JWT가 블랙리스트에 존재합니다.", HttpStatus.UNAUTHORIZED);
			}

			// JWT 검증 후 사용자 정보 헤더 추가
			ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
				.header("X-User-Id", claims.getSubject())
				.build();

			return chain.filter(exchange.mutate().request(modifiedRequest).build());

		} catch (ExpiredJwtException e) {
			log.error("JWT 만료됨: {}", e.getMessage());
			return onError(exchange, "JWT가 만료되었습니다.", HttpStatus.UNAUTHORIZED);
		} catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
			log.error("잘못된 JWT: {}", e.getMessage());
			return onError(exchange, "잘못된 JWT입니다.", HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			log.error("JWT 검증 중 오류 발생: {}", e.getMessage());
			return onError(exchange, "JWT 검증 실패", HttpStatus.UNAUTHORIZED);
		}
	}

	// JWT 검증
	private Claims validateToken(String token) {
		return Jwts.parser()
			.setSigningKey(Base64.getEncoder().encodeToString(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
			.parseClaimsJws(token)
			.getBody();
	}

	// 토큰이 블랙리스트에 있는지 확인
	private boolean isTokenBlacklisted(String token) {
		String blacklistKey = "blacklist:" + token;
		return redisTemplate.hasKey(blacklistKey); // Redis에서 해당 토큰이 블랙리스트에 존재하는지 체크
	}

	// 에러 응답 처리
	private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus status) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(status);
		return response.setComplete();
	}

	@Override
	public int getOrder() {
		return -1; // 필터 우선순위 설정 (낮을수록 먼저 실행됨)
	}
}
