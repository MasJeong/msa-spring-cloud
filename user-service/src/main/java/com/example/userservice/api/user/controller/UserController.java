package com.example.userservice.api.user.controller;

import com.example.userservice.api.user.domain.User;
import com.example.userservice.api.user.dto.UserDto;
import com.example.userservice.api.user.service.UserService;
import com.example.userservice.api.user.vo.RequestUser;
import com.example.userservice.api.user.vo.ResponseUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final Environment env;

    /**
     * User Service 상태 체크
     *
     * @return 환경 정보
     */
    @GetMapping("/health-check")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
    // TODO 아래 주석 테스트 필요
//    @RequireAdminOrCompanyRole
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

        ResponseUser responseUser = userService.getUserByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }

    /**
     * 사용자 정보 저장
     *
     * @param requestUser 저장할 요청 정보
     * @return responseUser
     */
    @PostMapping
    public ResponseEntity<ResponseUser> createUser(@RequestBody @Valid RequestUser requestUser) {

        UserDto userDto = modelMapper.map(requestUser, UserDto.class);

        ResponseUser responseUser = userService.createUser(userDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

}
