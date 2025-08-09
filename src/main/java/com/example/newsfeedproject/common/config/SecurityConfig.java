package com.example.newsfeedproject.common.config;

import com.example.newsfeedproject.auth.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //CSRF 끄기
                // TODO : 활성화하기
                .csrf(csrf -> csrf.disable())
                // /auth/** 는 인증 없이 허용, 나머지는 모두 차단
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**" ).permitAll()
                        // 이외에는 모두 허용이지만(개발 편의)
                        .anyRequest().denyAll()
                        // 아래 코드 나중에 활성화하여 로그인 필요 추가할 예정
                 //      .anyRequest().authenticated()
                )
                 .addFilterBefore(
                jwtAuthorizationFilter,
                UsernamePasswordAuthenticationFilter.class
                         //아직 JWT 발급이 된 건 아님
        );

        return http.build();
    }
}
