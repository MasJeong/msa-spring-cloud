package com.example.apigatewayservice.config;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Actuator 환경 설정
 */
@Configuration
public class ActuatorConfig {

    /**
     * HTTP 요청 및 응답 관리 bean 생성
     * @return HTTP 요청 및 응답 관리 bean
     */
    @Bean
    public HttpExchangeRepository httpExchangeRepository() {
        return new InMemoryHttpExchangeRepository();
    }
}
