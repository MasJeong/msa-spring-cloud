package com.example.userservice.com.security;

import com.example.userservice.com.support.RepositoryTestSupport;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
class JwtRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private Environment env;

    @Test
    @DisplayName("JWT 생성 및 파싱")
    void testCreateAndParseToken() {
        // given
        String username = "testuser1";
        String secret = env.getProperty("token.secret");
        assert secret != null;

        log.debug("== secret: {}", secret);

        // 비밀 키 생성
        byte[] secretKeyBytes = Base64.getEncoder().encode(secret.getBytes(StandardCharsets.UTF_8));
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        String expireTime = env.getProperty("token.expiration-time");
        assert expireTime != null;

        Instant now = Instant.now();

        String roleName = "ROLE_ADMIN";

        // Generate JWT
        String token = Jwts.builder()
                .subject(username)
                .claim("ROLE", roleName)
                .expiration(Date.from(now.plusMillis(Long.parseLong(expireTime))))  // 만료 시간
                .issuedAt(Date.from(now))   // 발급 시간
                .signWith(secretKey)        // 서명
                .compact();

        // when (JWT parse)
        Claims payload = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String roleValue = String.valueOf(payload.get("ROLE"));
        log.debug("== roleValue: {}", roleValue);

        // then
        assertNotNull(token);
        assertEquals(username, payload.getSubject());
        assertEquals(roleName, roleValue);
    }

}