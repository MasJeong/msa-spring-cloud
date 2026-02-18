package com.example.catalogservice.com.msgqueue;

import lombok.*;

/**
 * 카탈로그에서 주문 서비스로 전달하는 재고 처리 결과 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StockResultEvent {

    private String eventType;
    private String orderId;
    private String productId;
    private boolean success;
    private String reason;
    private Integer remainingStock;
}
