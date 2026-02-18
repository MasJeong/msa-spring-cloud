package com.example.orderservice.order.dto;

import lombok.*;

/**
 * 카탈로그 재고 변경 요청 이벤트 페이로드.
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
