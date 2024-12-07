package com.example.apigatewayservice.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * 사용자 정의 필터 구현 클래스
 * API 요청에 대한 인증 필터를 구현한다.
 */
@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final Environment env;

    public static final String TOKEN_TYPE = "Bearer ";

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    /**
     * Filter Config
     */
    @Getter
    public static class Config {
        private String tokenType = TOKEN_TYPE;
    }

    /**
     * API 요청에 대한 인증 필터 구현
     * @param config Filter Config
     * @return 인증 검증 수행 후 다음 필터로 요청 전달
     */
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "no authorization header");
            }

            List<String> headers = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
            assert headers != null;

            String authorizationHeader = headers.get(0);

            String jwt = authorizationHeader.replace(config.getTokenType(), "");

            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT is not valid");
            }

            Claims claims = getClaims(jwt);
            assert claims != null;

            String userId = String.valueOf(claims.get("sub"));

            ServerHttpRequest newRequest = request.mutate()
                    .header("X-USER-ID", userId)
                    .build();

            // 수정된 요청으로 객체 생성
            return chain.filter(exchange.mutate().request(newRequest).build());
        });
    }

    /**
     * jwt 유효성 검사
     * @param token 토큰 값
     * @return jwt 유효 여부 (유효하면 true, 유효하지 않으면 false)
     */
    private boolean isJwtValid(String token) {
        if(StringUtils.isEmpty(token)) return false;

        Claims claims = getClaims(token);

        if (claims == null) return false;

        return !StringUtils.isEmpty(String.valueOf(claims));
    }

    /**
     * JWT 클레임을 가져온다.
     * @param token JWT
     * @return JWT 클레임
     */
    private @Nullable Claims getClaims(String token) {
        Claims payload;

        try {
            String secret = env.getProperty("token.secret");
            Assert.notNull(secret, "secret should not be empty");

            // 비밀 키 생성
            byte[] secretKeyBytes = Base64.getEncoder().encode(secret.getBytes(StandardCharsets.UTF_8));
            SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

            // JWT parse
            payload = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

        return payload;
    }

    /**
     * 오류 응답을 생성
     * @param exchange     현재의 ServerWebExchange 객체
     * @param errorMessage 오류 메시지
     * @return Mono<Void> 오류 응답을 비동기적으로 처리하는 단일값 (web flux)
     */
    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        log.error(errorMessage);

        return response.setComplete();
    }
}
