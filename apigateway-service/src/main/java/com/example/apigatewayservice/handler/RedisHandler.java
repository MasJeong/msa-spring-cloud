package com.example.apigatewayservice.handler;

import com.example.apigatewayservice.config.RedisConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * Redis 연산 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisHandler {

    private final RedisConfig redisConfig;

    /**
     * 리스트에 접근하여 다양한 연산을 수행합니다.
     * @return ListOperations<String, Object>
     */
    public ListOperations<String, Object> getListOperations() {
        return redisConfig.redisTemplate().opsForList();
    }

    /**
     * 단일 데이터에 접근하여 다양한 연산을 수행합니다.
     * @return ValueOperations<String, Object>
     */
    public ValueOperations<String, Object> getValueOperations() {
        return redisConfig.redisTemplate().opsForValue();
    }


    /**
     * Redis 작업중 등록, 수정, 삭제에 대해서 처리 및 예외처리를 수행합니다.
     * @param operation operation
     * @return 성공적으로 실행되면 1을 반환하고, 예외가 발생하면 0을 반환한다.
     */
    public int executeOperation(Runnable operation) {
        try {
            operation.run();
            return 1;
        } catch (Exception e) {
            log.error("An error occurred while working with redis: {}", e.getMessage());
            return 0;
        }
    }
}
