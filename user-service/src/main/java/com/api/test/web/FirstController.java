package com.api.test.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/first")
public class FirstController {

    Environment env;

    @Autowired
    public FirstController(Environment env) {
        this.env = env;
    }

    @GetMapping("/check")
    public String check (HttpServletRequest request){
        log.info("Server port = {}", request.getServerPort());

        return String.format("This is a message from First Service on PORT $s",
                env.getProperty("local.server.port"));
    }
}