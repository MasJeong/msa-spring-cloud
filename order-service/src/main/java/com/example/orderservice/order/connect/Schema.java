package com.example.orderservice.order.connect;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Schema {

    private String type;

    private List<Field> fields;

    private boolean optional;

    private String name;
}
