package com.example.userservice.com.security;

import com.example.userservice.user.dto.UserDto;
import com.example.userservice.user.service.UserService;
import com.example.userservice.user.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;

    private final Environment env;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager,
                                      UserService userService,
                                      Environment env) {
        super(authenticationManager);
        this.userService = userService;
        this.env = env;
    }

    /**
     * 사용자 인증을 처리 메서드.
     * 클라이언트로부터 전달된 인증 정보를 바탕으로 사용자 인증을 처리하고 인증 결과를 반환한다.
     * @param request request
     * @param response response
     * @return 인증 성공 시 Authentication 객체 반환, 인증 실패 시 AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        try {
            RequestLogin credential = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            /*
            authenticate: 사용자 인증을 수행
            UsernamePasswordAuthenticationToken: 사용자의 인증 정보를 담는 객체
             */
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credential.getEmail(),
                            credential.getPwd(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication auth) {

        String userName = ((User) auth.getPrincipal()).getUsername();
        UserDto userDetails = userService.getUserDetailsByEmail(userName);

        String secret = env.getProperty("token.secret");
        assert secret != null;

        byte[] secretKeyBytes = Base64.getEncoder().encode(secret.getBytes());

        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        Instant now = Instant.now();

        String expireTime = env.getProperty("token.expiration-time");
        assert expireTime != null;

        String token = Jwts.builder()
                .subject(userDetails.getUserId())
                .expiration(Date.from(now.plusMillis(Long.parseLong(expireTime))))
                .issuedAt(Date.from(now))
                .signWith(secretKey)
                .compact();

        response.addHeader("token", token);
//        response.addHeader("userId", userDetails.getUserId());
    }
}
