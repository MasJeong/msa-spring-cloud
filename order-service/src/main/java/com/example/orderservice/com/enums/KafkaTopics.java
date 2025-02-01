package com.example.orderservice.com.enums;

import lombok.Getter;

@Getter
public enum KafkaTopics {

    CATALOG_STOCK_UPDATE("example-catalog-topic"),
    ORDER_INSERT("orders");

    private final String topicName;

    KafkaTopics(String topicName) {
        this.topicName = topicName;
    }

}
