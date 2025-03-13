package com.oingmaryho.infrastructure.gateway_service.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

  // 인증 예외 처리할 경로 목록
  private static final List<String> EXCLUDED_PATHS = Arrays.asList("/api/v1/users/signup",
      "/api/v1/users/login");
  @Value("${spring.secrets.secretKey}")
  private static String SECRET_KEY;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange,
      org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getURI().getPath();

    if (EXCLUDED_PATHS.contains(path)) {
      return chain.filter(exchange);
    }

    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      log.warn("JWT 토큰이 없습니다.");
      return onError(exchange, "JWT 토큰이 필요합니다.", HttpStatus.UNAUTHORIZED);
    }

    String token = authHeader.substring(7);

    try {
      Claims claims = validateToken(token);
      log.info("JWT 검증 성공: {}", claims);

      ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
          .header("X-User-Id", claims.getSubject())
          .header("X-User-Roles", claims.get("roles", String.class))
          .header("X-User-Slack-Id", claims.get("slackId", String.class))
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

  private Claims validateToken(String token) {
    return Jwts.parser().setSigningKey(
            Base64.getEncoder().encodeToString(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
        .parseClaimsJws(token).getBody();
  }

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
