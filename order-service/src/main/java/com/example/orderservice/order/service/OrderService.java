package com.example.orderservice.order.service;

import com.example.orderservice.order.domain.OrderEntity;
import com.example.orderservice.order.dto.OrderDto;
import com.example.orderservice.order.repository.OrderRepository;
import com.example.orderservice.order.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final ModelMapper modelMapper;

    private final OrderRepository orderRepository;

    /**
     * 주문 정보 저장
     * @param orderDto 주문 정보
     * @return 주문 정보
     */
    public OrderDto createOrder(OrderDto orderDto) {
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDto.getQty() * orderDto.getUnitPrice());

        OrderEntity order = modelMapper.map(orderDto, OrderEntity.class);

        OrderEntity createdOrder = orderRepository.save(order);

        return modelMapper.map(createdOrder, OrderDto.class);
    }

    /**
     * 주문 상세정보 조회
     * @param orderId 주문 ID
     * @return 주문 상세정보
     */
    public OrderDto getOrderByOrderId(String orderId) {
        Optional<OrderEntity> opOrder = orderRepository.findByOrderId(orderId);
        OrderEntity order = opOrder.orElseThrow();

        return modelMapper.map(order, OrderDto.class);
    }

    /**
     * 사용자 주문 목록 조회
     * @param userId 사용자 ID
     * @return 사용자 주문 목록
     */
    public List<ResponseOrder> getOrdersByUserId(String userId) {
        return Optional.ofNullable(orderRepository.findByUserId(userId))
                .orElseGet(Collections::emptyList)
                .stream()
                .map(order -> modelMapper.map(order, ResponseOrder.class))
                .toList();
    }
}
