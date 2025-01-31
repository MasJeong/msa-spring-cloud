package com.example.orderservice.order.controller;

import com.example.orderservice.com.enums.KafkaTopics;
import com.example.orderservice.com.msgqueue.KafkaProducer;
import com.example.orderservice.order.dto.OrderDto;
import com.example.orderservice.order.service.OrderService;
import com.example.orderservice.order.vo.RequestOrder;
import com.example.orderservice.order.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order-service")
public class OrderController {

    /** 주문 서비스 */
    private final OrderService orderService;

    /** 객체간 매핑 mapper */
    private final ModelMapper modelMapper;

    private final KafkaProducer kafkaProducer;

    /**
     * 주문 목록 조회
     * @param userId 사용자 아이디
     * @return 주문 정보
     */
    @GetMapping("{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrders(@PathVariable("userId") String userId) {

        List<ResponseOrder> responseOrders = orderService.getOrdersByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(responseOrders);
    }

    /**
     * 주문 저장
     * @param requestOrder 저장할 요청 정보
     * @param userId 사용자 아이디
     * @return 주문 결과 정보
     */
    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                    @RequestBody RequestOrder requestOrder) {

        OrderDto orderDto = modelMapper.map(requestOrder, OrderDto.class);
        orderDto.setUserId(userId);

        OrderDto createdOrderDto = orderService.createOrder(orderDto);

        ResponseOrder responseOrder = modelMapper.map(createdOrderDto, ResponseOrder.class);

        /* send this order to the kafka */
        kafkaProducer.send(KafkaTopics.CATALOG_STOCK_UPDATE.getTopicName(), orderDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

}
