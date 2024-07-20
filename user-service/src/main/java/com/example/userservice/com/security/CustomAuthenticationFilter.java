package com.example.userservice.com.security;

import com.example.userservice.user.service.UserService;
import com.example.userservice.user.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, Environment env) {

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
            getAuthenticationManager: 인증을 처리하는 메서드
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
                                            Authentication authResult) throws IOException, ServletException {


//        super.successfulAuthentication(request, response, chain, authResult);
    }
}
