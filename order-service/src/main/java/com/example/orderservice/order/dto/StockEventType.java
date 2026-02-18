package com.example.orderservice.order.dto;

import lombok.Getter;

/**
 * 주문-카탈로그 간 재고 Saga 이벤트 타입 정의.
 */
@Getter
public enum StockEventType {

    CATALOG_STOCK_DECREASE("CATALOG_STOCK_DECREASE"),
    CATALOG_STOCK_DECREASE_SUCCEEDED("CATALOG_STOCK_DECREASE_SUCCEEDED"),
    CATALOG_STOCK_DECREASE_FAILED("CATALOG_STOCK_DECREASE_FAILED");

    private final String value;

    StockEventType(String value) {
        this.value = value;
    }
}
