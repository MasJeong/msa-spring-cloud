package com.example.orderservice.order.dto;

import lombok.*;

/**
 * 카탈로그가 재고 처리 결과를 응답할 때 사용하는 이벤트 DTO.
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
