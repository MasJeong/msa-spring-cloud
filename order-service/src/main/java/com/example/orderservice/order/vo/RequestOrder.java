package com.example.orderservice.order.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RequestOrder {

    @NotNull(message = "User ID cannot be null")
    private String productId;

    @NotNull(message = "Name cannot be null")
    @Size(min = 2, message = "Name not be less than two characters")
    private Integer qty;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be equal or greater than 8 characters")
    private Integer unitPrice;
}
