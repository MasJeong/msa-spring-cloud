package com.example.catalogservice.com.msgqueue;

import com.example.catalogservice.catalog.service.CatalogService;
import com.example.catalogservice.com.enums.KafkaTopics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 카탈로그 재고 이벤트를 수신해 차감/복원 처리하고 결과를 발행하는 Kafka consumer.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final CatalogService catalogService;
    private final ObjectMapper objectMapper;
    private final KafkaProducer kafkaProducer;

    /**
     * Kafka 메시지 처리 리스너
     * example-catalog-topic 토픽으로부터 메시지를 수신하여 재고 업데이트.
     * @param kafkaMessage Kafka로부터 수신한 JSON 형식의 메시지 문자열
     */
    @KafkaListener(topics = "#{T(com.example.catalogservice.com.enums.KafkaTopics).CATALOG_STOCK_UPDATE.getTopicName()}")
    public void processKafkaMessage(String kafkaMessage) {
        log.debug("Kafka Message: {}", kafkaMessage);

        StockEvent event = parseKafkaMessage(kafkaMessage);
        if (event == null
                || !StringUtils.hasText(event.getEventType())
                || !StringUtils.hasText(event.getOrderId())
                || !StringUtils.hasText(event.getProductId())
                || event.getQty() == null) {
            log.error("Invalid message format. payload: {}", kafkaMessage);
            return;
        }

        if (StockEventType.CATALOG_STOCK_DECREASE.getValue().equals(event.getEventType())) {
            applyStockDecrease(event);
            return;
        }

        if (StockEventType.CATALOG_STOCK_RESTORE.getValue().equals(event.getEventType())) {
            applyStockRestore(event);
            return;
        }

        log.warn("Unsupported eventType: {}", event.getEventType());
    }

    /**
     * Kafka 메시지 파싱
     * @param kafkaMessage JSON 형식의 Kafka 메시지 문자열
     * @return 파싱된 StockEvent 객체. 파싱 실패 시 null 반환
     */
    private StockEvent parseKafkaMessage(String kafkaMessage) {
        try {
            return objectMapper.readValue(kafkaMessage, StockEvent.class);
        } catch (JsonProcessingException ex) {
            log.error("Error parsing Kafka message: {}", kafkaMessage, ex);
            return null;
        }
    }

    /**
     * 재고 차감 이벤트를 처리하고 결과 이벤트를 응답 토픽으로 발행한다.
     */
    private void applyStockDecrease(StockEvent event) {
        try {
            int remainingStock = catalogService.decreaseStock(event.getProductId(), event.getQty());
            publishResult(event.getOrderId(), event.getProductId(), true, null, remainingStock,
                    StockEventType.CATALOG_STOCK_DECREASE_SUCCEEDED.getValue());
            log.info("Updated stock for product: {}. New stock: {}", event.getProductId(), remainingStock);
        } catch (IllegalArgumentException e) {
            log.error("Stock event invalid: {}", e.getMessage());
            publishResult(event.getOrderId(), event.getProductId(), false, e.getMessage(), null,
                    StockEventType.CATALOG_STOCK_DECREASE_FAILED.getValue());
        } catch (IllegalStateException e) {
            log.error("Stock event failed: {}", e.getMessage());
            publishResult(event.getOrderId(), event.getProductId(), false, e.getMessage(), null,
                    StockEventType.CATALOG_STOCK_DECREASE_FAILED.getValue());
        } catch (Exception e) {
            log.error("Unexpected stock update failure", e);
            publishResult(event.getOrderId(), event.getProductId(), false, "unexpected error", null,
                    StockEventType.CATALOG_STOCK_DECREASE_FAILED.getValue());
        }
    }

    /**
     * 보상 트랜잭션으로 재고를 복원한다.
     */
    private void applyStockRestore(StockEvent event) {
        try {
            int remainingStock = catalogService.restoreStock(event.getProductId(), event.getQty());
            log.info("Restored stock for product: {}. New stock: {}", event.getProductId(), remainingStock);
        } catch (Exception e) {
            log.error("Stock restore failed. orderId={}, productId={}, qty={}",
                    event.getOrderId(), event.getProductId(), event.getQty(), e);
        }
    }

    /**
     * 재고 처리 결과를 주문 서비스로 응답 메시지 형태로 발행한다.
     */
    private void publishResult(String orderId, String productId, boolean success, String reason, Integer remainingStock,
                               String eventType) {
        StockResultEvent result = StockResultEvent.builder()
                .eventType(eventType)
                .orderId(orderId)
                .productId(productId)
                .success(success)
                .reason(reason)
                .remainingStock(remainingStock)
                .build();

        kafkaProducer.send(KafkaTopics.CATALOG_STOCK_UPDATE_RESULT.getTopicName(), result);
    }
}
