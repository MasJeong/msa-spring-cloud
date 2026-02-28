package com.example.orderservice.order.repository;

import com.example.orderservice.order.domain.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from OrderEntity o where o.orderId = :orderId")
    int deleteByOrderId(@Param("orderId") String orderId);

    /**
     * 주문 목록을 조회한다.
     *
     * @param userId 사용자 ID
     * @return 주문 목록
     */
    List<OrderEntity> findByUserId(String userId);
}
