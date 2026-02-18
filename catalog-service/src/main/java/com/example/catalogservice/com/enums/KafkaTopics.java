package com.example.catalogservice.com.enums;

import lombok.Getter;

/**
 * catalog-service에서 사용하는 Kafka 토픽 정의.
 */
@Getter
public enum KafkaTopics {

    CATALOG_STOCK_UPDATE("example-catalog-topic"),
    /** 카탈로그 처리 결과를 주문 서비스에 전달할 토픽 */
    CATALOG_STOCK_UPDATE_RESULT("example-catalog-topic-result");

    private final String topicName;

    KafkaTopics(String topicName) {
        this.topicName = topicName;
    }
}
