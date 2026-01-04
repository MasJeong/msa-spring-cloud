package com.example.userservice.api.user.client.feign;

import com.example.userservice.api.user.vo.ResponseOrder;
// import org.springframework.cloud.openfeign.FeignClient; // gRPC 전환으로 주석 처리
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Order Service OpenFeign 클라이언트 (gRPC 전환으로 비활성화)
 */
//@FeignClient(name = "order-service", configuration = FeignErrorDecoder.class, url = "${order-service-url}")
// gRPC로 전환하여 비활성화 (아래 주석 처리)
//@FeignClient(name = "order-service")
public interface OrderServiceClient {
  
    /**
     * 사용자 주문 목록 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 주문 목록
     */
    @GetMapping("/order-service/{userId}/orders")
    List<ResponseOrder> getOrders(@PathVariable("userId") String userId);
}
