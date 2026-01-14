package com.example.userservice.api.user.controller;

import com.example.userservice.api.user.client.grpc.OrderServiceGrpcClient;
import com.example.userservice.api.user.domain.User;
import com.example.userservice.api.user.dto.UserDto;
import com.example.userservice.api.user.service.UserService;
import com.example.userservice.api.user.vo.RequestUser;
import com.example.userservice.api.user.vo.ResponseOrder;
import com.example.userservice.api.user.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 Controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final Environment env;

    /** 주문 service gRPC Client */
    private final OrderServiceGrpcClient orderServiceGrpcClient;

    /** 서킷브레이커 */
    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;

    /**
     * User Service 상태 체크
     *
     * @return 환경 정보
     */
    @GetMapping("/health-check")
    @Timed(value = "users.healthCheck", longTask = true)
    public String healthCheck() {
        return String.format("It's working in User Service, " +
                        "port(local.server.port)=%s, " +
                        "port(server.port)=%s, " +
                        "token secret=%s, " +
                        "token expiration time=%s",
                env.getProperty("local.server.port"),
                env.getProperty("server.port"),
                env.getProperty("token.secret"),
                env.getProperty("token.expiration-time")
        );
    }

    /**
     * 사용자 전체 목록 조회
     *
     * @return 사용자 목록
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ResponseUser>> getUsers() {

        List<User> userList = userService.getAllUsers();
        List<ResponseUser> responseUsers = new ArrayList<>();

        userList.forEach(user -> responseUsers.add(modelMapper.map(user, ResponseUser.class)));

        return ResponseEntity.status(HttpStatus.OK).body(responseUsers);
    }

    /**
     * 사용자 상세정보 조회
     *
     * @param userId 사용자 아이디
     * @return 사용자 상세정보
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserByUserId(userId);

        log.debug("before call orders microservice (gRPC)");

        /*
         * OpenFeign (REST/HTTP)에서 gRPC로 변경한 이유:
         * 1. 성능 개선: 레이턴시 약 50% 감소 
         * 2. 처리량 증가: 초당 처리량 2-3배 증가
         * 3. 리소스 효율: 네트워크 대역폭 약 50% 감소
         * 4. HTTP/2 멀티플렉싱 및 Protobuf 바이너리 직렬화로 인한 오버헤드 감소
         */
        // 서킷 브레이커 패턴 적용 (gRPC)
        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("cb-userToOrder-grpc");
        List<ResponseOrder> orders = circuitbreaker.run(() -> orderServiceGrpcClient.getOrders(userId),
                throwable -> {
                    log.warn("gRPC call failed, returning empty list", throwable);
                    return new ArrayList<>();
                }
        );
        log.debug("after called orders microservice (gRPC)");

        userDto.setOrders(orders);

        ResponseUser responseUser = modelMapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }

    /**
     * 사용자 정보 저장
     *
     * @param requestUser 저장할 요청 정보
     * @return responseUser
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseUser> createUser(@RequestBody @Valid RequestUser requestUser) {

        UserDto userDto = modelMapper.map(requestUser, UserDto.class);

        User saveUser = userService.createUser(userDto);

        ResponseUser responseUser = modelMapper.map(saveUser, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

}
