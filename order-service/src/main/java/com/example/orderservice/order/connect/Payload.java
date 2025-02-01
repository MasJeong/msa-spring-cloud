package com.example.orderservice.order.connect;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Payload {

    private String order_id;

    private String user_id;

    private String product_id;

    private int qty;

    private int unit_price;

    private int total_price;
}
