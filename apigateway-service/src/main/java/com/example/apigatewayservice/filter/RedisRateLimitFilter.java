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
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
//@Component
public class RedisRateLimitFilter extends AbstractGatewayFilterFactory<RedisRateLimitFilter.Config> {

    private final KeyResolver userKeyResolver;

    private final RedisRateLimiter redisRateLimiter;

    public RedisRateLimitFilter(KeyResolver userKeyResolver, RedisRateLimiter redisRateLimiter) {
        super(Config.class);
        this.userKeyResolver = userKeyResolver;
        this.redisRateLimiter = redisRateLimiter;
    }

    @Getter @Setter
    public static class Config implements HasRouteId {
        private String routeId;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String routeId = config.getRouteId();

            log.debug("routeId: {}", routeId);

            return userKeyResolver.resolve(exchange)
                    .flatMap(key -> redisRateLimiter.isAllowed(routeId, key))
                    .flatMap(rateLimitResponse -> {
                        // Rate limit이 허용된 경우
                        if (rateLimitResponse.isAllowed()) {
                            log.debug("Rate limit pass. ");

                            return chain.filter(exchange);
                        }

                        log.warn("Rate limit exceeded. ");

                        return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"));
                    });
        };
    }
}
