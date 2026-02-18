package com.example.orderservice.com.msgqueue;

import com.example.orderservice.com.enums.KafkaTopics;
import com.example.orderservice.order.dto.StockResultEvent;
import com.example.orderservice.order.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * 카탈로그 재고 처리 결과를 수신해 주문 보상 로직을 수행하는 Kafka consumer.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper mapper;
    private final OrderService orderService;

    /**
     * 카탈로그 보상/보정 결과 처리 리스너
     *
     * @param kafkaMessage 카탈로그에서 전달된 재고 차감 결과
     */
    @KafkaListener(topics = "#{T(com.example.orderservice.com.enums.KafkaTopics).CATALOG_STOCK_UPDATE_RESULT.getTopicName()}")
    public void handleStockResult(String kafkaMessage) {
        log.debug("Kafka stock result message: {}", kafkaMessage);

        StockResultEvent event = parseResultMessage(kafkaMessage);
        if (event.getOrderId() == null || event.getOrderId().isBlank()) {
            log.error("Invalid stock result. orderId is missing: {}", kafkaMessage);
            return;
        }

        if (event.isSuccess()) {
            log.info("Stock update succeeded for orderId: {}", event.getOrderId());
            return;
        }

        log.warn("Stock update failed for orderId: {}. reason={}", event.getOrderId(), event.getReason());
        orderService.cancelOrder(event.getOrderId());
        log.info("Compensation applied: order {} removed", event.getOrderId());
    }

    /**
     * 카탈로그 응답 메시지를 StockResultEvent로 변환한다.
     *
     * @param kafkaMessage 수신 원문 JSON
     * @return 파싱된 이벤트(실패 시 기본 에러 이벤트)
     */
    private StockResultEvent parseResultMessage(String kafkaMessage) {
        try {
            return mapper.readValue(kafkaMessage, StockResultEvent.class);
        } catch (JsonProcessingException ex) {
            log.error("Error parsing stock result message: {}", kafkaMessage, ex);
            return defaultResult();
        }
    }

    /**
     * 파싱 실패 시 보상 처리를 강제하기 위한 기본 실패 이벤트를 생성한다.
     */
    private StockResultEvent defaultResult() {
        return StockResultEvent.builder()
                .orderId("")
                .success(false)
                .reason("invalid message format")
                .build();
    }
}
