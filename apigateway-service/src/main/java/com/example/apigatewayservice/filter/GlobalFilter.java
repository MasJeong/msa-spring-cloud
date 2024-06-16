package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Global Filter 구현
 */
@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        super(Config.class);
    }

    /**
     * Global Pre Filter
     * @param config config class
     * @return GatewayFilter bean
     */
    @Override
    public GatewayFilter apply(Config config) {
        // Global Pre Filter
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global PRE Filter: baseMessage -> {}", config.getBaseMessage());

            if (config.isPreLogger()) {
                log.info("Global Filter Start: request id -> {}", request.getId());
            }

            /*
            Global Post Filter
            chain.filter(exchange): post 필터 적용하기 위해 chain에 추가한다.
            Mono.fromRunnable: (spring 5버전 이상) web flux 비동기 방식의 서버에서 단일값 전달할 때 Mono class를 사용한다.
             */
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("Global Filter End: response code -> {}", response.getStatusCode());
                }
            }));
        });
    }

    /**
     * configuration 정보를 세팅한다.
     */
    @Data
    public static class Config {
        private String baseMessage;

        private boolean preLogger;

        private boolean postLogger;

    }

}
