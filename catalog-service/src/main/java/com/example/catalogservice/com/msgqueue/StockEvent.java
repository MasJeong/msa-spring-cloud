package com.example.catalogservice.com.msgqueue;

import lombok.*;

/**
 * 주문-카탈로그 간 재고 변경 요청 이벤트 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StockEvent {

    private String eventType;
    private String orderId;
    private String userId;
    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;
}
