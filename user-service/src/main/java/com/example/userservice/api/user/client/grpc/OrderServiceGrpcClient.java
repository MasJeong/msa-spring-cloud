package com.example.userservice.api.user.client.grpc;

import com.example.orderservice.grpc.GetOrdersRequest;
import com.example.orderservice.grpc.GetOrdersResponse;
import com.example.orderservice.grpc.Order;
import com.example.orderservice.grpc.OrderServiceGrpc;
import com.example.userservice.api.user.vo.ResponseOrder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order Service와의 gRPC 통신을 담당하는 클라이언트
 */
@Slf4j
@Component
public class OrderServiceGrpcClient {

    @GrpcClient("order-service")
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * 사용자별 주문 목록을 gRPC를 통해 조회합니다.
     * 
     * @param userId 조회할 사용자 ID
     * @return 사용자 주문 목록
     */
    public List<ResponseOrder> getOrders(String userId) {
        try {
            log.debug("gRPC: Calling getOrdersByUserId for userId: {}", userId);

            GetOrdersRequest request = GetOrdersRequest.newBuilder()
                    .setUserId(userId)
                    .build();

            GetOrdersResponse response = orderServiceStub.getOrdersByUserId(request);

            return response.getOrdersList().stream()
                    .map(this::convertToResponseOrder)
                    .collect(Collectors.toList());

        } catch (StatusRuntimeException e) {
            log.error("gRPC: Error calling getOrdersByUserId for userId: {}", userId, e);
            return new ArrayList<>();
        }
    }

    /**
     * gRPC Order 메시지를 ResponseOrder VO로 변환합니다.
     * 
     * @param grpcOrder 변환할 gRPC Order 메시지
     * @return 변환된 ResponseOrder 객체
     */
    private ResponseOrder convertToResponseOrder(Order grpcOrder) {
        ResponseOrder responseOrder = new ResponseOrder();
        responseOrder.setOrderId(grpcOrder.getOrderId());
        responseOrder.setProductId(grpcOrder.getProductId());
        responseOrder.setQty(grpcOrder.getQty());
        responseOrder.setUnitPrice(grpcOrder.getUnitPrice());
        responseOrder.setTotalPrice(grpcOrder.getTotalPrice());
        
        String createdAt = grpcOrder.getCreatedAt();

        if (!StringUtils.hasText(createdAt)) {
            return responseOrder;
        }

        try {
            responseOrder.setCreatedAt(LocalDateTime.parse(createdAt, FORMATTER));
        } catch (Exception e) {
            log.warn("Failed to parse createdAt: {}", createdAt, e);
        }

        return responseOrder;
    }
}
