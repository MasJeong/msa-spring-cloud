package com.example.userservice.com.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final Environment environment;

    @GetMapping(value = "/health-check")
    public String healthCheck() {
        return "It's Working in User Service";
    }

    @GetMapping(value = "/welcome")
    public String welcome() {
        return environment.getProperty("greeting.message");
    }

}
