package com.example.newsfeedproject;

import com.example.newsfeedproject.common.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import lombok.extern.slf4j.Slf4j;
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
    @EnableJpaAuditing
@Slf4j
public class NewsfeedProjectApplication {

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    public static void main(String[] args) {
        SpringApplication.run(NewsfeedProjectApplication.class, args);
    }
    // 서버 가동 시 한 번만, 비밃키 일겅와서 JWT서명/검증 알고리즘을 준비해두는 작업임.
    @PostConstruct
    public void initJwt() {
        JwtUtil.init(jwtSecret);
        log.info("JwtUtil이 비밀로 초기화되었습니다.");
    }
}
