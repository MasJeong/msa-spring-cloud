package com.example.orderservice.order.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestOrder {

    @NotNull(message = "Product ID cannot be null. ")
    private String productId;

    @NotNull(message = "Qty cannot be null")
    @Size(min = 1, max = 11, message = "The Qty can be a minimum of 1 characters and a maximum of 11 characters. ")
    private Integer qty;

    @NotNull(message = "UnitPrice cannot be null")
    @Size(min = 3, max = 11, message = "The unitPrice can be a minimum of 3 characters and a maximum of 11 characters. ")
    private Integer unitPrice;
}
