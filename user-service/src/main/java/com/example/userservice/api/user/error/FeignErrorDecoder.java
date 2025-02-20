package com.example.userservice.api.user.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

/**
 * Feign 요청 시 Exception 처리하는 클래스
 */
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

    private final Environment env;

    /**
     * HTTP 응답 예외처리
     * @param methodKey 호출된 메서드 키
     * @param response  HTTP 응답 객체
     * @return 해당 상태 코드에 따른 Exception
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> methodKey.contains("getOrders")
                    ? new ResponseStatusException(
                            HttpStatusCode.valueOf(response.status()),
                            env.getProperty("order_service.exception.orders_is_empty"))
                    : new Exception(response.reason());
            default -> new Exception(response.reason());
        };
    }
}
