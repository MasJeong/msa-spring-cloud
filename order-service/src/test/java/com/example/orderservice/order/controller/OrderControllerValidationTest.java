package com.example.orderservice.order.controller;

import com.example.orderservice.com.config.UtilConfig;
import com.example.orderservice.com.msgqueue.KafkaProducer;
import com.example.orderservice.order.dto.OrderDto;
import com.example.orderservice.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 주문 생성 요청 검증(@Valid) 동작을 DB·Kafka 없이 확인한다.
 */
class OrderControllerValidationTest {

    private static final String ORDER_URL = "/order-service/{userId}/orders";
    private static final String USER_ID = "user-1";

    private OrderService orderService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        OrderController controller = new OrderController(orderService, new UtilConfig().modelMapper(),
                mock(KafkaProducer.class));
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("수량이 0이면 400을 반환하고 주문을 저장하지 않는다")
    void createOrder_zeroQty_returnsBadRequest() throws Exception {
        // When
        ResultActions result = postOrder("""
                {"productId": "CATALOG-001", "qty": 0, "unitPrice": 1500}
                """);

        // Then
        result.andExpect(status().isBadRequest());
        verify(orderService, never()).createOrder(any());
    }

    @Test
    @DisplayName("단가가 음수이면 400을 반환하고 주문을 저장하지 않는다")
    void createOrder_negativeUnitPrice_returnsBadRequest() throws Exception {
        // When
        ResultActions result = postOrder("""
                {"productId": "CATALOG-001", "qty": 1, "unitPrice": -100}
                """);

        // Then
        result.andExpect(status().isBadRequest());
        verify(orderService, never()).createOrder(any());
    }

    @Test
    @DisplayName("상품 ID가 비어 있으면 400을 반환하고 주문을 저장하지 않는다")
    void createOrder_blankProductId_returnsBadRequest() throws Exception {
        // When
        ResultActions result = postOrder("""
                {"productId": " ", "qty": 1, "unitPrice": 1500}
                """);

        // Then
        result.andExpect(status().isBadRequest());
        verify(orderService, never()).createOrder(any());
    }

    @Test
    @DisplayName("유효한 요청이면 주문을 생성하고 201을 반환한다")
    void createOrder_validRequest_returnsCreated() throws Exception {
        // Given
        when(orderService.createOrder(any())).thenReturn(OrderDto.builder()
                .orderId("order-1")
                .userId(USER_ID)
                .productId("CATALOG-001")
                .qty(2)
                .unitPrice(1500)
                .totalPrice(3000)
                .build());

        // When
        ResultActions result = postOrder("""
                {"productId": "CATALOG-001", "qty": 2, "unitPrice": 1500}
                """);

        // Then
        result.andExpect(status().isCreated());
        verify(orderService).createOrder(any());
    }

    private ResultActions postOrder(String body) throws Exception {
        return mockMvc.perform(post(ORDER_URL, USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }
}
