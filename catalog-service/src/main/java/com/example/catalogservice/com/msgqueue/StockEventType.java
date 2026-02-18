package com.example.catalogservice.com.msgqueue;

import lombok.Getter;

/**
 * catalog-service 재고 이벤트 타입 정의.
 */
@Getter
public enum StockEventType {

    CATALOG_STOCK_DECREASE("CATALOG_STOCK_DECREASE"),
    CATALOG_STOCK_DECREASE_SUCCEEDED("CATALOG_STOCK_DECREASE_SUCCEEDED"),
    CATALOG_STOCK_DECREASE_FAILED("CATALOG_STOCK_DECREASE_FAILED"),
    CATALOG_STOCK_RESTORE("CATALOG_STOCK_RESTORE");

    private final String value;

    StockEventType(String value) {
        this.value = value;
    }
}
