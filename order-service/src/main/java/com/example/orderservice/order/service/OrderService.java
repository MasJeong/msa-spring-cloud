package com.example.orderservice.order.service;

import com.example.orderservice.order.domain.OrderEntity;
import com.example.orderservice.order.dto.OrderDto;
import com.example.orderservice.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 주문 엔티티의 생성, 조회, 보상(취소) 로직을 담당한다.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final ModelMapper modelMapper;

    private final OrderRepository orderRepository;

    /**
     * 주문 정보를 생성하고 주문 ID 및 합계 금액을 계산해 저장한다.
     * @param orderDto 주문 정보
     * @return 주문 정보
     */
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDto.getQty() * orderDto.getUnitPrice());

        OrderEntity order = modelMapper.map(orderDto, OrderEntity.class);

        OrderEntity createdOrder = orderRepository.save(order);

        return modelMapper.map(createdOrder, OrderDto.class);
    }

    /**
     * 분산 트랜잭션 보상 단계에서 주문을 삭제해 롤백한다.
     *
     * @param orderId 주문 ID
     */
    @Transactional
    public void cancelOrder(String orderId) {
        orderRepository.findByOrderId(orderId).ifPresent(orderRepository::delete);
    }

    /**
     * 주문 ID로 주문 단건을 조회한다.
     *
     * @param orderId 주문 ID
     * @return 주문 DTO
     */
    @Transactional(readOnly = true)
    public OrderDto getOrder(String orderId) {
        OrderEntity order = orderRepository.findByOrderId(orderId).orElseThrow();

        return modelMapper.map(order, OrderDto.class);
    }

    /**
     * 주문 ID로 주문 상세 정보를 조회한다.
     * @param orderId 주문 ID
     * @return 주문 상세정보
     */
    @Transactional(readOnly = true)
    public OrderDto getOrderByOrderId(String orderId) {
        Optional<OrderEntity> opOrder = orderRepository.findByOrderId(orderId);
        OrderEntity order = opOrder.orElseThrow();

        return modelMapper.map(order, OrderDto.class);
    }

    /**
     * 사용자 ID 기준으로 주문 목록을 조회한다.
     *
     * @param userId 사용자 ID
     * @return 사용자 주문 목록
     */
    @Transactional(readOnly = true)
    public List<OrderEntity> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
