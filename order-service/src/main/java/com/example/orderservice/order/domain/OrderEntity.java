package com.example.orderservice.order.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String productId;

    @Column(nullable = false)
    private Integer qty;

    @Column(nullable = false)
    private Integer unitPrice;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

}
