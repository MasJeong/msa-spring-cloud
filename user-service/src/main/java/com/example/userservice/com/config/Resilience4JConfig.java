package com.example.userservice.com.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * Resilience4J 설정
 * 서킷 브레이커와 타임 리미터의 글로벌 설정을 정의합니다.
 */
@Configuration
public class Resilience4JConfig {

    /**
     * Resilience4J CustomCircuitBreaker Bean 생성
     * <p>
     * from CLOSED to OPEN:
     * - 실패율 50% 초과 (failureRateThreshold)
     * - 느린 호출율 30% 초과 (slowCallRateThreshold)
     * -> 느린 호출 기준 시간 5초
     * <p>
     * from OPEN to HALF-OPEN:
     * - 60초 경과 후 (waitDurationInOpenState)
     * <p>
     * from HALF-OPEN to CLOSED:
     * - 20개의 요청 중 호출 실패율과 느린 호출율이 임계값 이하
     * <p>
     * from HALF-OPEN to OPEN:
     * - 20개의 요청 중 실패율 또는 느린 호출율이 임계값 초과
     *
     * @return Custom CircuitBreakerFactory
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> configureResilience4JCircuitBreaker() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                // 호출 실패율 (50%)
                .failureRateThreshold(50)
                /*
                느린 호출율: slowCall 발생 시 서버 스레드 점유로 인해 장애가 생길 수 있으므로 기본값(100)보다 작게 설정
                너무 낮으면 일시적인 문제에도 과민반응할 수 있고, 너무 높으면 심각한 문제를 늦게 감지함
                 */
                .slowCallRateThreshold(30)
                /*
                 느린 호출 기준 시간 5초
                 해당 값은 TimeLimiter의 timeoutDuration보다 작아야 함
                 */
                .slowCallDurationThreshold(Duration.ofSeconds(5L))
                /*
                Open 상태 지속 시간 (60초)
                OPEN 상태에서 HALF-OPEN 상태로 전환되기 전에 대기하는 시간
                서킷이 열린 상태를 유지하는 지속 시간
                 */
                .waitDurationInOpenState(Duration.ofSeconds(60L))
                /*
                슬라이딩 윈도우: CLOSED 상태에서 호출 결과를 기록하고 집계하며 다음 2가지 방식으로 구분된다.
                마지막 N번의 호출 결과를 기반으로 하는 count-based sliding window(횟수 기반 슬라이딩 윈도우)
                마지막 N초의 결과를 기반으로 하는 time-based sliding window(시간 기반 슬라이딩 윈도우)
                 */
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(20)
                /*
                HALF-OPEN 상태에서 20개의 호출을 허용
                20개의 호출이라면 시스템 회복 여부를 판단하기 괜찮을 것 같다.
                 */
                .permittedNumberOfCallsInHalfOpenState(20)
                .minimumNumberOfCalls(20) // 최소 20번 호출 후에 실패율을 계산
                .recordExceptions(IOException.class, TimeoutException.class) // 해당 예외들을 실패로 기록한다.
//                .ignoreExceptions()
                .build();

        // 타임 리미터 설정: 외부 서비스 호출이나 시간이 오래 걸릴 수 있는 작업에 대해 제한 시간
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                // 타임아웃 시간 6초 - slowCallDurationThreshold 보다는 크게 설정되어야 함
                .timeoutDuration(Duration.ofSeconds(6L))
                .build();

        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig)
                .circuitBreakerConfig(circuitBreakerConfig)
                .build());
    }
}
