package com.example.orderservice.com.msgqueue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 주문 서비스에서 Kafka 메시지를 발행하는 공통 producer wrapper.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 객체를 JSON 문자열로 직렬화해 지정한 토픽으로 전송한다.
     *
     * @param topic   전송 대상 Kafka 토픽
     * @param payload 직렬화할 메시지 객체
     */
    public void send(String topic, Object payload) {
        String jsonInString = "";

        try {
            jsonInString = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("Error parsing Kafka payload:", e);
            throw new IllegalStateException("Failed to convert Kafka payload to JSON", e);
        }

        try {
            kafkaTemplate.send(topic, jsonInString).get(3, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Kafka send interrupted", ex);
        } catch (ExecutionException | TimeoutException ex) {
            throw new IllegalStateException("Kafka send failed", ex);
        }
        log.info("Kafka Producer sent data from the Order MicroService: {}", payload);
    }
}
