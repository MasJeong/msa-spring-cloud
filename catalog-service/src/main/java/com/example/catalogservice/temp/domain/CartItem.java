package com.example.catalogservice.temp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

    /** 상품 ID */
    private String productId;

    /** 상품 수량 */
    private int quantity;

    /** 상품 금액 */
    private BigDecimal price;
}