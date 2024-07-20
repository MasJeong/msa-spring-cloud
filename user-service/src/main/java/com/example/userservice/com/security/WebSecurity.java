package com.example.userservice.com.security;

import com.example.userservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {

    private final UserService userService;

    private final AuthenticationConfiguration authenticationConfiguration;

    private final Environment env;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/users/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/health-check/**").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/**").access((authentication, request) -> {
                            String clientIp = request.getRequest().getRemoteAddr();
                            return new AuthorizationDecision(clientIp.equals("127.0.0.1"));
                        })
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .addFilter(getAuthenticationFilter(authenticationConfiguration));

        return http.build();
    }


    private CustomAuthenticationFilter getAuthenticationFilter(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return new CustomAuthenticationFilter(authenticationManager(authenticationConfiguration), userService, env);
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}