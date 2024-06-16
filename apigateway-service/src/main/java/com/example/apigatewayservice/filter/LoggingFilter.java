package com.example.apigatewayservice.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Logging Filter 구현
 */
@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    /**
     * Logging Pre Filter
     * @param config config class
     * @return GatewayFilter bean
     */
    @Override
    public GatewayFilter apply(Config config) {
        // Logging Pre Filter
        return new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging PRE Filter: baseMessage -> {}", config.getBaseMessage());

            if (config.isPreLogger()) {
                log.info("Logging Filter Start: request id -> {}", request.getId());
            }

            // Logging Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("Logging Filter End: response code -> {}", response.getStatusCode());
                }
            }));
        }, OrderedGatewayFilter.LOWEST_PRECEDENCE); // filter의 우선순위 LOWEST_PRECEDENCE: 제일 낮아 가장 나중에 실행됨
    }

    /**
     * configuration 정보를 세팅한다.
     */
    @Getter
    @Setter
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

}
