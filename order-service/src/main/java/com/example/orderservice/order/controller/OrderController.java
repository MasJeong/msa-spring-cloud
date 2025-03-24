package com.example.orderservice.order.controller;

import com.example.orderservice.com.msgqueue.KafkaProducer;
import com.example.orderservice.com.msgqueue.OrderProducer;
import com.example.orderservice.order.domain.OrderEntity;
import com.example.orderservice.order.dto.OrderDto;
import com.example.orderservice.order.service.OrderService;
import com.example.orderservice.order.vo.RequestOrder;
import com.example.orderservice.order.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order-service")
public class OrderController {

    /** 주문 서비스 */
    private final OrderService orderService;

    /** 객체간 매핑 mapper */
    private final ModelMapper modelMapper;

    /** 주문 producer */
    private final OrderProducer orderProducer;

    /** Kafka producer */
    private final KafkaProducer kafkaProducer;

    /**
     * 주문 목록 조회
     *
     * @param userId 사용자 아이디
     * @return 주문 정보
     */
    @GetMapping("{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrders(@PathVariable("userId") String userId) {
        List<OrderEntity> orders = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> responseOrders = new ArrayList<>();

        orders.forEach(user -> responseOrders.add(modelMapper.map(orders, ResponseOrder.class)));

        return ResponseEntity.status(HttpStatus.OK).body(responseOrders);
    }

    /**
     * 주문 저장
     *
     * @param orderDetails 저장할 요청 정보
     * @param userId 사용자 아이디
     * @return 주문 결과 정보
     */
    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {

        log.info("before add orders data");

        OrderDto orderDto = modelMapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);

        /* JPA */
        OrderDto createdOrderDto = orderService.createOrder(orderDto);
        ResponseOrder responseOrder = modelMapper.map(createdOrderDto, ResponseOrder.class);

//        orderDto.setOrderId(UUID.randomUUID().toString());
//        orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());

        /*
        Kafka를 통해 주문 처리의 후속 단계인 재고 업데이트를 비동기적으로 수행하여
        주문 프로세스의 응답 시간을 개선한다.
         */
//        kafkaProducer.send(KafkaTopics.CATALOG_STOCK_UPDATE.getTopicName(), orderDto);

        /* 분산된 DB인 경우 kafka connect sink-connector를 사용하여 동기화  */
//        orderProducer.send(KafkaTopics.ORDER_INSERT.getTopicName(), orderDto);

//        ResponseOrder responseOrder = modelMapper.map(orderDto, ResponseOrder.class);

        log.info("after added orders data");

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

}
