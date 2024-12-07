package com.example.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Custom Filter 구현
 * Global Filter(전역 필터)와는 다르게 원하는 서비스에 filter를 설정하는 경우 사용한다.
 */
@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter() {
        super(Config.class);
    }

    /**
     * Custom Pre Filter
     * @param config config class
     * @return GatewayFilter bean
     */
    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Custom PRE Filter: request id -> {}", request.getId());

            /*
            Custom Post Filter
            chain.filter(exchange): post 필터 적용하기 위해 chain에 추가한다.
            Mono.fromRunnable: (spring 5버전 이상) web flux 비동기 방식의 서버에서 단일값 전달할 때 Mono class를 사용한다.
             */
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Custom POST Filter: response code -> {}", response.getStatusCode());
            }));
        });
    }

    /**
     * configuration 정보를 세팅한다.
     */
    public static class Config {

    }

}
