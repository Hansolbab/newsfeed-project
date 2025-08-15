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
    public static void main(String[] args) {
        SpringApplication.run(NewsfeedProjectApplication.class, args);
    }

}
