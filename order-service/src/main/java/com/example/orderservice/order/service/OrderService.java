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

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final ModelMapper modelMapper;

    private final OrderRepository orderRepository;

    public OrderDto createOrder(OrderDto orderDto) {
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDto.getQty() * orderDto.getUnitPrice());

        OrderEntity order = modelMapper.map(orderDto, OrderEntity.class);

        OrderEntity createdOrder = orderRepository.save(order);

        return modelMapper.map(createdOrder, OrderDto.class);
    }

    public OrderDto getOrderByOrderId(String orderId) {
        Optional<OrderEntity> opOrder = orderRepository.findByOrderId(orderId);
        OrderEntity order = opOrder.orElseThrow();

        return modelMapper.map(order, OrderDto.class);
    }

    public List<OrderEntity> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
