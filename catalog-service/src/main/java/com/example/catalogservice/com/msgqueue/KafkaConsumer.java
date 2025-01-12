package com.example.catalogservice.com.msgqueue;

import com.example.catalogservice.catalog.repository.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final CatalogRepository catalogRepository;

    /**
     * Kafka 메시지 처리 리스너
     * example-catalog-topic 토픽으로부터 메시지를 수신하여 재고 업데이트.
     * @param kafkaMessage Kafka로부터 수신한 JSON 형식의 메시지 문자열
     */
    @KafkaListener(topics = "example-catalog-topic")
    public void processKafkaMessage(String kafkaMessage) {
        log.info("Kafka Message: {}", kafkaMessage);

        Map<String, Object> messageMap = parseKafkaMessage(kafkaMessage);

        String productId = (String) messageMap.get("productId");
        Integer qty = (Integer) messageMap.get("qty");

        if (!StringUtils.hasText(productId) || qty == null) {
            log.error("Invalid message format. productId: {}, quantity: {}", productId, qty);
            return;
        }

        updateCatalogStock(productId, qty);
    }

    /**
     * Kafka 메시지 파싱
     * @param kafkaMessage JSON 형식의 Kafka 메시지 문자열
     * @return 파싱된 메시지를 담은 Map 객체. 파싱 실패 시 빈 Map 반환
     */
    private Map<String, Object> parseKafkaMessage(String kafkaMessage) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(kafkaMessage, new TypeReference<>() {});
        } catch (JsonProcessingException ex) {
            log.error("Error parsing Kafka message: {}", kafkaMessage, ex);
            return Collections.emptyMap();
        }
    }

    /**
     * 지정된 제품의 재고를 업데이트합니다.
     * @param productId 제품 ID
     * @param qty 재고 수량
     */
    private void updateCatalogStock(String productId, int qty) {
        Optional.ofNullable(catalogRepository.findByProductId(productId)).ifPresent(entity -> {
            entity.setStock(entity.getStock() - qty);
            catalogRepository.save(entity);
            log.info("Updated stock for product: {}. New stock: {}", productId, entity.getStock());
        });
    }
}
