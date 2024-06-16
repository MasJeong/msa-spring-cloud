package com.example.firstservice.sample.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/first-service")
public class FirstServiceController {

    private final Environment env;

    public FirstServiceController(Environment env) {
        this.env = env;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the first service. ";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("first-request") String header) {
        log.info("header value = {}", header);
        return "Hello World in First Service";
    }

    @GetMapping("/check")
    public String check(HttpServletRequest request) {
        /*
        1개의 서비스를 다중 포트로 여러 개의 앱을 띄워 로드밸런싱을 테스트하였다.
        spring cloud gateway의 로드밸런싱 기본 알고리즘은 라운드로빈 방식이다.
         */
        log.info("Server port={}", request.getServerPort());
        return String.format("hi, there. this is a message from first service on PORT %s", env.getProperty("local.server.port"));
    }
}
