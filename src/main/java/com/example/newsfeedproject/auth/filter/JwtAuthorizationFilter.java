package com.example.newsfeedproject.auth.filter;


import com.example.newsfeedproject.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    //JWT 관련 기능을 담당하는 유틸 클래스 (토큰 꺼내기, 검증 등)
    //이 필터가 JWT → 유저 복원 → 인증 컨텍스트 세팅을 해주는 다리
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        //HTTP 요청 헤더에서 Authorization 값을 꺼냄 (토큰 담겨 있음)
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // 토큰 유효성 검사 (만료됐는지, 위조됐는지 등)
            //!"refresh".equals(subject)로 리프레시 토큰은 인증에 못쓰게 차단
            if (JwtUtil.validateToken(token) && !"refresh".equals(JwtUtil.getUserNameFromToken(token))) {
                String email = JwtUtil.getUserNameFromToken(token);
                //DB에서 유저 조회
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                //현재 요청 스레드의 보안 컨텍스트에 저장
                //여기서 var는 컴파일러가 추론하기 때문에, 명시 생략
                var auth = new UsernamePasswordAuthenticationToken(
                        //여기서 null은 자격 증명(보통 비밀번호)
                        //여기서 userDetails.getAuthorities()는 사용자의 권한 목록(빈 리스트여도 무방함)
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        //다음 필터로 넘어가게 해줌 (필수!)
        filterChain.doFilter(request, response);
    }
}
