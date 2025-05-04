package com.example.userservice.com.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WebDAV 서버 연결을 위한 Sardine 클라이언트 설정 클래스
 * application.yml의 webdav 설정 항목을 주입받아 사용
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

    private String baseUrl;


    /**
     * WebDAV 서버 연결을 위한 Sardine 빈 생성
     *
     * @return 구성된 Sardine 클라이언트 인스턴스
     */
    @Bean
    public Sardine webDavClient() {
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials(username, password);
        return sardine;
    }

}