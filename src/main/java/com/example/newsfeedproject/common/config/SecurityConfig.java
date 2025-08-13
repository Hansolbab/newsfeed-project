package com.example.newsfeedproject.common.config;

import com.example.newsfeedproject.common.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    //TODO: 브라우저에서 쿠키 보내기 - 도전
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())//CSRF 끄기
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 무상태
                .formLogin(f->f.disable())
                .httpBasic(h->h.disable())
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/api/auth/signUp", "/api/auth/signIn" ).permitAll()
                                .requestMatchers("/api/auth/signOut").hasRole("USER") // USER 권한(로그인한 사람)만 로그아웃 가능
                                .requestMatchers("/api/**").hasRole("USER")  // 현재는 로그인한 사람들만 다 볼 수 있게 설정
                                .requestMatchers("/error").permitAll()
                                .anyRequest().authenticated()
                )
                 .addFilterBefore(
                jwtAuthorizationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
