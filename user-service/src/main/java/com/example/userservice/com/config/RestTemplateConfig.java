package com.example.userservice.com.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate Bean 생성
     * - @LoadBalanced : Spring Cloud에서 제공하는 로드밸런싱 기능으로 Eureka 등의 서비스 등록 및 검색 기능과 같이 사용되며
     *                   default 알고리즘은 라운드 로빈이다.
     * @return RestTemplate Bean
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
