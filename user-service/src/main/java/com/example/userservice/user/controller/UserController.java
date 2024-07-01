package com.example.userservice.user.controller;

import com.example.userservice.user.dto.UserDto;
import com.example.userservice.user.service.UserService;
import com.example.userservice.user.vo.RequestUser;
import com.example.userservice.user.vo.ResponseUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final Environment environment;

    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody RequestUser requestUser) {

        UserDto userDto = modelMapper.map(requestUser, UserDto.class);

        userService.createUser(userDto);

        ResponseUser responseUser = modelMapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping(value = "/health-check")
    public String healthCheck() {
        return String.format("hi, %s", environment.getProperty("local.server.port"));
    }
}
