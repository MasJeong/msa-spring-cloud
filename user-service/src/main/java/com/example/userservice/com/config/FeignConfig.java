package com.example.userservice.com.config;

import com.example.userservice.api.user.error.FeignErrorDecoder;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class FeignConfig {

    /**
     * Feign Logger Level Bean 생성
     * @return FeignClient Logger Level Bean
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;   // HTTP 요청에 대한 모든 정보로 설정
    }

    /**
     * Feign ErrorDecoder Bean 생성
     * @return Feign ErrorDecoder Bean
     */
    @Bean
    public FeignErrorDecoder feignErrorDecoder(Environment env) {
        return new FeignErrorDecoder(env);
    }
}
