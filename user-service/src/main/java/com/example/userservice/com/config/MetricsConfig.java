package com.example.userservice.com.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Micrometer를 활용하여 애플리케이션의 메트릭 데이터를 수집하고 기록하기 위한 설정
 * - Micrometer의 메트릭 수집 기능 활성화
 * - @Timed 어노테이션을 통해 메서드 실행 시간 측정
 * - 측정된 메트릭 데이터를 MeterRegistry를 통해 Prometheus와 같은 모니터링 시스템으로 전송 가능
 */
@Configuration
@EnableAspectJAutoProxy
public class MetricsConfig {

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

}
