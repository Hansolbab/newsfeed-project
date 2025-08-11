package com.example.newsfeedproject.common.config;

import com.example.newsfeedproject.common.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy; //한솔용
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
                //CSRF 끄기
                // TODO : 활성화하기
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 무상태 // 한솔용
                .formLogin(f->f.disable()) // 한솔용
                .httpBasic(h->h.disable())// 한솔용
                // /auth/** 는 인증 없이 허용, 나머지는 모두 차단
                .authorizeHttpRequests(
                        auth -> auth
//                        .requestMatchers("/**" ).permitAll()
                                //auth signup, signin만 허용
                                .requestMatchers("/api/auth/signup", "/api/auth/signin").permitAll()// 한솔용
                                //hasRole String형식으로 들어감
                                .requestMatchers("/api/auth/signout").hasRole("USER") // USER 권한(로그인한 사람)만 로그아웃 가능
                                .requestMatchers("/api/**").hasRole("USER")  // 현재는 로그인한 사람들만 다 볼 수 있게 설정
                                .anyRequest().authenticated()// 한솔용
                        // 이외에는 모두 허용이지만(개발 편의)
//                        .anyRequest().denyAll()
                )
                 .addFilterBefore(
                jwtAuthorizationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
