package com.example.catalogservice.com.msgqueue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * catalog-service에서 Kafka 이벤트 발행을 담당하는 프로듀서.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 객체를 JSON으로 직렬화해 지정 토픽으로 전송한다.
     *
     * @param topic   전송 대상 Kafka 토픽
     * @param payload 직렬화 대상 메시지
     */
    public void send(String topic, Object payload) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";

        try {
            jsonInString = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("Error parsing Kafka payload:", e);
            throw new IllegalStateException("Failed to convert Kafka payload to JSON", e);
        }

        try {
            kafkaTemplate.send(topic, jsonInString).get(3, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new IllegalStateException("Kafka send failed", ex);
        }
        log.info("Kafka Producer sent data from the Catalog MicroService: {}", payload);
    }
}
