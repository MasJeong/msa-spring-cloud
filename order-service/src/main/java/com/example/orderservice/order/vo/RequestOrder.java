package com.example.orderservice.order.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestOrder {

    @NotBlank(message = "Product ID cannot be blank")
    private String productId;

    @NotNull(message = "Qty cannot be null")
    @Min(value = 1, message = "Qty must be at least 1")
    private Integer qty;

    @NotNull(message = "UnitPrice cannot be null")
    @Min(value = 1, message = "UnitPrice must be at least 1")
    private Integer unitPrice;
}
