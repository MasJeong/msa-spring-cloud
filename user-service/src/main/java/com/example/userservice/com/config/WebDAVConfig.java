package com.example.userservice.com.config;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WebDAV 서버 연결을 위한 Sardine 클라이언트 설정 클래스
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "webdav")
public class WebDAVConfig {

    /** ID */
    private String username;

    /** PW */
    private String password;

    /** WebDAV Base URL */
    private String baseUrl;

    /**
     * WebDAV 클라이언트 라이브러리 Bean
     *
     * @return WebDAV 클라이언트 빈
     */
    @Bean
    public Sardine sardine() {
        Sardine sardine = SardineFactory.begin(username, password);

        // 사전 인증 적용
        sardine.enablePreemptiveAuthentication(baseUrl, 80, -1);

        return sardine;
    }

}