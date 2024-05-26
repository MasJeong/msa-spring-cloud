package com.example.secondservice.sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/second-service")
public class SecondServiceController {

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the second service. ";
    }
}