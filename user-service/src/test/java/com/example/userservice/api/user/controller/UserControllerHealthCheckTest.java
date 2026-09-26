package com.example.userservice.api.user.controller;

import com.example.userservice.api.user.client.grpc.OrderServiceGrpcClient;
import com.example.userservice.api.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * health-check 응답에 JWT 서명 키가 노출되지 않는지 DB·Config Server 없이 확인한다.
 */
class UserControllerHealthCheckTest {

    private static final String HEALTH_CHECK_URL = "/users/health-check";
    private static final String TOKEN_SECRET = "test-token-secret-must-not-leak";

    @Test
    @DisplayName("health-check 응답에 token.secret 값이 포함되지 않는다")
    void healthCheck_doesNotExposeTokenSecret() throws Exception {
        // Given
        MockEnvironment env = new MockEnvironment()
                .withProperty("token.secret", TOKEN_SECRET)
                .withProperty("token.expiration-time", "1800000");

        // When
        String body = requestHealthCheck(env);

        // Then
        assertFalse(body.contains(TOKEN_SECRET), "token.secret must not be exposed: " + body);
        assertTrue(body.contains("token secret configured=true"), body);
        assertTrue(body.contains("token expiration time=1800000"), body);
    }

    @Test
    @DisplayName("token.secret이 주입되지 않으면 configured=false를 반환한다")
    void healthCheck_reportsMissingTokenSecret() throws Exception {
        // Given
        MockEnvironment env = new MockEnvironment();

        // When
        String body = requestHealthCheck(env);

        // Then
        assertTrue(body.contains("token secret configured=false"), body);
    }

    private String requestHealthCheck(MockEnvironment env) throws Exception {
        UserController controller = new UserController(mock(UserService.class), new ModelMapper(), env,
                mock(OrderServiceGrpcClient.class), mock(Resilience4JCircuitBreakerFactory.class));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        return mockMvc.perform(get(HEALTH_CHECK_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
