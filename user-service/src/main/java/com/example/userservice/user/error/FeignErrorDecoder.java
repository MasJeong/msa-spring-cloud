package com.example.userservice.user.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

/**
 * Feign 요청 시 Exception 처리하는 클래스
 */
public class FeignErrorDecoder implements ErrorDecoder {

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
                    ? new ResponseStatusException(HttpStatusCode.valueOf(response.status()), "User's orders is empty")
                    : new Exception(response.reason());
            default -> new Exception(response.reason());
        };
    }
}
