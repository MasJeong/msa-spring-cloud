package com.example.apigatewayservice.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RedisRateLimitFilter extends AbstractGatewayFilterFactory<RedisRateLimitFilter.Config> {

    private final KeyResolver userKeyResolver;

    private final RedisRateLimiter redisRateLimiter;

    public RedisRateLimitFilter(KeyResolver userKeyResolver, RedisRateLimiter redisRateLimiter) {
        super(Config.class);
        this.userKeyResolver = userKeyResolver;
        this.redisRateLimiter = redisRateLimiter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            KeyResolver keyResolver = getOrDefault(config.keyResolver, userKeyResolver);
            RedisRateLimiter rateLimiter1 = getOrDefault(config.rateLimiter, redisRateLimiter);

            String routeId = config.getRouteId();

            log.debug("routeId: {}", routeId);

            return keyResolver.resolve(exchange)
                    .flatMap(key -> rateLimiter1.isAllowed(routeId, key))
                    .flatMap(rateLimitResponse -> {
                        if (rateLimitResponse.isAllowed()) {
                            log.debug("Rate limit pass. ");

                            return chain.filter(exchange);  // Rate limit이 허용된 경우
                        } else {
                            log.warn("Rate limit failed. ");

                            return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"));
                        }
                    });
        };
    }

    private <T> T getOrDefault(T configValue, T defaultValue) {
        return configValue != null ? configValue : defaultValue;
    }

    @Getter @Setter
    public static class Config implements HasRouteId {
        private KeyResolver keyResolver;
        private RedisRateLimiter rateLimiter;
        private String routeId;
    }
}
