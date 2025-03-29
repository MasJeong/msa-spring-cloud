package com.example.orderservice.order.repository;

import com.example.orderservice.order.domain.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    /**
     * 주문 정보를 조회한다.
     *
     * @param orderId 주문 ID
     * @return 주문 상세정보
     */
    Optional<OrderEntity> findByOrderId(String orderId);

    /**
     * 주문 목록을 조회한다.
     *
     * @param userId 사용자 ID
     * @return 주문 목록
     */
    List<OrderEntity> findByUserId(String userId);
}
