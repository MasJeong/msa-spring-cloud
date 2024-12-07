package com.example.apigatewayservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

/**
 * 사용자 ID를 사용하여 Rate Limit 기능을 사용한다.
 */
@Slf4j
@Configuration
public class RateLimiterConfig {

    /**
     * RequestRateLimiter Filter 기준 Bean 생성
     * @return 사용자 ID
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();

            String userId = String.valueOf(request.getHeaders().get("X-USER-ID"));
            log.debug("request userId : {}", userId);

            return Mono.just(StringUtils.hasText(userId) ? userId : "Anonymous");
        };
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 1, 1);
    }

    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new ReactiveStringRedisTemplate((ReactiveRedisConnectionFactory) redisConnectionFactory);
    }
}
