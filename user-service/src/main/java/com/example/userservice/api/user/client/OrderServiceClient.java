package com.example.userservice.api.user.client;

import com.example.userservice.api.user.vo.ResponseOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
  
    /**
     * 사용자 주문 목록 조회
     * @param userId 사용자 ID
     * @return 사용자 주문 목록
     */
    @GetMapping("/order-service/{userId}/orders")
    List<ResponseOrder> getOrders(@PathVariable("userId") String userId);
}
