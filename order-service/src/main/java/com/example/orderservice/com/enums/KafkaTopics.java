package com.example.orderservice.com.enums;

import lombok.Getter;

/**
 * order-service에서 사용하는 Kafka 토픽 정의 모음.
 */
@Getter
public enum KafkaTopics {

    /** 주문 생성 시 카탈로그의 재고 차감/보정 요청을 전송하는 토픽 */
    CATALOG_STOCK_UPDATE("example-catalog-topic"),
    /** 카탈로그 결과를 수신하는 토픽 */
    CATALOG_STOCK_UPDATE_RESULT("example-catalog-topic-result"),
    /** 기존 Order 이벤트 송신 용도(현재는 미사용) */
    ORDER_INSERT("orders");

    private final String topicName;

    KafkaTopics(String topicName) {
        this.topicName = topicName;
    }

}
