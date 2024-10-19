package com.example.userservice.com.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.Arrays;
import java.util.concurrent.Executor;

@Slf4j
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);    // 비동기 요청 피크 시간대를 고려하여 설정 (+ 부하 테스트 후)
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();

        return new DelegatingSecurityContextAsyncTaskExecutor(executor); // 비동기 작업 시 security context 전달
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, obj) -> log.error("비동기 예외 발생 - 메서드: {}, 파라미터: {}, 예외: {}",
                method.getName(), Arrays.toString(obj), throwable);
    }
}
