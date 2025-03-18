package com.example.userservice.com.security;

import com.example.userservice.com.security.constants.JwtConstant;
import com.google.common.net.HttpHeaders;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 사용자 정의 JWT 검증 Filter
 */
public class CustomJwtValidationFilter extends OncePerRequestFilter {

    private final Environment env;

    private final UserDetailsService userService;

    /** JWT 인증을 제외할 요청 목록 */
    private final List<String> excludePaths = Arrays.asList("/login", "/health-check", "/actuator", "/error");

    public CustomJwtValidationFilter(Environment env, UserDetailsService userService) {
        this.env = env;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain chain) throws IOException, ServletException {

        if(isExcludePath(request.getServletPath())) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(!StringUtils.hasText(header) || !header.startsWith(JwtConstant.TOKEN_PREFIX)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing Authorization header");
            return;
        }

        String token = header.substring(7);

        try {
            String secret = env.getProperty("token.secret");
            Assert.notNull(secret, "secret should not be empty");

            // 비밀 키 생성
            byte[] secretKeyBytes = Base64.getEncoder().encode(secret.getBytes(StandardCharsets.UTF_8));
            SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            List<?> roleObjects = claims.get("roles", List.class);

            List<SimpleGrantedAuthority> authorities = roleObjects.stream()
                    .filter(obj -> obj instanceof Map)  // LinkedHashMap 타입 확인
                    .map(obj -> (Map<?, ?>) obj)
                    .map(roleMap -> new SimpleGrantedAuthority((String) roleMap.get("authority")))
                    .collect(Collectors.toList());

            // 인증 객체 생성
            UserDetails userDetails = userService.loadUserByUsername(claims.getSubject());
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, authorities
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ExpiredJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
            return;
        } catch (JwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An error occurred while processing the token");
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * JWT 인증 제외 여부를 확인한다.
     *
     * @param servletPath 요청 Path
     * @return JWT 인증 제외 여부
     */
    private boolean isExcludePath(String servletPath) {
        if(!StringUtils.hasText(servletPath)) return false;

        return excludePaths.stream()
                .anyMatch(servletPath::startsWith);
    }
}
