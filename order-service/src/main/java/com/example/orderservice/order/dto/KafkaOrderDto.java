package com.example.orderservice.order.dto;

import com.example.orderservice.order.connect.Payload;
import com.example.orderservice.order.connect.Schema;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@ToString
public class KafkaOrderDto implements Serializable {

    private Schema schema;

    private Payload payload;
}
