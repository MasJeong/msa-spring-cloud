package com.example.orderservice.order.controller;

import com.example.orderservice.com.enums.KafkaTopics;
import com.example.orderservice.com.msgqueue.KafkaProducer;
import com.example.orderservice.order.domain.OrderEntity;
import com.example.orderservice.order.dto.OrderDto;
import com.example.orderservice.order.dto.StockEvent;
import com.example.orderservice.order.dto.StockEventType;
import com.example.orderservice.order.service.OrderService;
import com.example.orderservice.order.vo.RequestOrder;
import com.example.orderservice.order.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 주문 API 컨트롤러.
 * 주문 생성 시 Saga 보상 흐름을 위해 Kafka 재고 차감 이벤트를 발행한다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order-service")
public class OrderController {

    /** 주문 비즈니스 로직을 처리하는 서비스 */
    private final OrderService orderService;

    /** DTO/VO 변환 전용 매퍼 */
    private final ModelMapper modelMapper;

//    /** 주문 producer */
//    private final OrderProducer orderProducer;

    /** Kafka 메시지 발행을 담당하는 컴포넌트 */
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

        orders.forEach(order -> responseOrders.add(modelMapper.map(order, ResponseOrder.class)));

        return ResponseEntity.status(HttpStatus.OK).body(responseOrders);
    }

    /**
     * 주문 저장
     *
     * @param orderDetails 주문 요청 본문
     * @param userId 주문자 ID
     * @return 생성된 주문 정보
     */
    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {

        OrderDto orderDto = modelMapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);

        /* JPA */
        OrderDto createdOrderDto = orderService.createOrder(orderDto);
        ResponseOrder responseOrder = modelMapper.map(createdOrderDto, ResponseOrder.class);

        StockEvent stockEvent = StockEvent.builder()
                .eventType(StockEventType.CATALOG_STOCK_DECREASE.getValue())
                .orderId(createdOrderDto.getOrderId())
                .userId(createdOrderDto.getUserId())
                .productId(createdOrderDto.getProductId())
                .qty(createdOrderDto.getQty())
                .unitPrice(createdOrderDto.getUnitPrice())
                .totalPrice(createdOrderDto.getTotalPrice())
                .build();

        /*
        Kafka를 통해 주문 처리의 후속 단계인 재고 업데이트를 비동기적으로 수행하여
        주문 프로세스의 응답 시간을 개선한다.
         */
        try {
            kafkaProducer.send(KafkaTopics.CATALOG_STOCK_UPDATE.getTopicName(), stockEvent);
        } catch (Exception ex) {
            orderService.cancelOrder(createdOrderDto.getOrderId());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "재고 처리 이벤트 전송 실패로 주문을 보상 처리했습니다.");
        }

        // 분산된 DB인 경우 kafka connect sink-connector를 사용하여 동기화
//        orderProducer.send(KafkaTopics.ORDER_INSERT.getTopicName(), orderDto);
//        ResponseOrder responseOrder = modelMapper.map(orderDto, ResponseOrder.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

}
