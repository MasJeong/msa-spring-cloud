package com.example.orderservice.order.grpc;

import com.example.orderservice.grpc.GetOrdersRequest;
import com.example.orderservice.grpc.GetOrdersResponse;
import com.example.orderservice.grpc.Order;
import com.example.orderservice.grpc.OrderServiceGrpc;
import com.example.orderservice.order.domain.OrderEntity;
import com.example.orderservice.order.service.OrderService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order Service gRPC 서버 구현
 * 
 * <p>User Service로부터 gRPC 요청을 받아 처리합니다.
 * OrderService의 기존 비즈니스 로직을 재사용합니다.
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class OrderGrpcServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderService orderService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * 사용자별 주문 목록 조회 (gRPC)
     *
     * @param request 조회 요청 (userId 포함)
     * @param responseObserver 응답 스트림
     */
    @Override
    public void getOrdersByUserId(GetOrdersRequest request, StreamObserver<GetOrdersResponse> responseObserver) {
        try {
            List<OrderEntity> orders = orderService.getOrdersByUserId(request.getUserId());

            GetOrdersResponse response = GetOrdersResponse.newBuilder()
                    .addAllOrders(orders.stream()
                            .map(this::convertToGrpcOrder)
                            .collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC: Error in GetOrdersByUserId for userId: {}", request.getUserId(), e);
            responseObserver.onError(e);
        }
    }

    /**
     * OrderEntity를 gRPC Order 메시지로 변환
     *
     * @param entity 변환할 OrderEntity
     * @return 변환된 gRPC Order 메시지
     */
    private Order convertToGrpcOrder(OrderEntity entity) {
        return Order.newBuilder()
                .setOrderId(entity.getOrderId())
                .setUserId(entity.getUserId())
                .setProductId(entity.getProductId())
                .setQty(entity.getQty())
                .setUnitPrice(entity.getUnitPrice())
                .setTotalPrice(entity.getTotalPrice())
                .setCreatedAt(entity.getCreatedAt().format(FORMATTER))
                .build();
    }
}

