package com.example.orderservice.order.connect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Field {

    private String type;

    private boolean optional;

    private String field;
}
