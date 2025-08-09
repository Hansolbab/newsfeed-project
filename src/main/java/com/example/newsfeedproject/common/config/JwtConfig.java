package com.example.newsfeedproject.common.config;

import com.example.newsfeedproject.common.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @PostConstruct
    public void init() {
         JwtUtil.init(jwtSecret);
    }
}
