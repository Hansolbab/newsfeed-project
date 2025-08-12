package com.example.newsfeedproject.common.filter;


import com.example.newsfeedproject.auth.impl.UserDetailsServiceImpl;
import com.example.newsfeedproject.common.util.JwtUtil;
import com.example.newsfeedproject.users.repository.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    //JWT 관련 기능을 담당하는 유틸 클래스 (토큰 꺼내기, 검증 등)
    //이 필터가 JWT → 유저 복원 → 인증 컨텍스트 세팅을 해주는 다리
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    private final UsersRepository usersRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        //HTTP 요청 헤더에서 Authorization 값을 꺼냄 (토큰 담겨 있음)
        String header = request.getHeader("Authorization");
        System.out.println("[JWT] Authorization header = " + header);
        // 로그인 스킵
        if ("POST".equalsIgnoreCase(request.getMethod())
                && ("/api/auth/signin".equals(request.getRequestURI())
               )) {
            filterChain.doFilter(request, response);
            return;
        }

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // 1) 토큰 유효성 먼저 확인
            if (JwtUtil.validateToken(token)) {
                // 2) 그 다음에만 이메일 추출 (만료/위조로 인한 500 방지)
                String email = JwtUtil.getUserEmailFromToken(token);
                if ("refresh".equals(email)) { // 리프레시 토큰은 인증에 사용 안 함
                    filterChain.doFilter(request, response);
                    return;
                }
                // 3) 탈퇴 엔드포인트 재요청이면 즉시 409
                boolean isWithdrawEndpoint =
                        "DELETE".equalsIgnoreCase(request.getMethod())
                                && "/api/myinfo/modify/delete".equals(request.getRequestURI());
                if (isWithdrawEndpoint) {
                    usersRepository.findByEmail(email).ifPresent(u -> {
                        if (Boolean.TRUE.equals(u.getDeleted())) {
                            try {
                                response.sendError(HttpServletResponse.SC_CONFLICT, "이미 탈퇴한 사용자입니다.");
                            } catch (IOException ignored) {}
                        }
                    });
                    if (response.isCommitted()) return; // 이미 409 보냈으면 종료
                }
                // 4) 일반 인증 흐름 (UserDetailsServiceImpl에서 deleted=false 필터링하도록)
                try {
                    userDetailsServiceImpl.loadUserByUsername(email); // 탈퇴/미존재면 예외 발생
                } catch (UsernameNotFoundException e) {
                    SecurityContextHolder.clearContext();// 인증 미세팅
                    filterChain.doFilter(request, response);
                    return;
                }
                Authentication auth = JwtUtil.getAuthentication(token, userDetailsServiceImpl);
                //현재 요청 스레드의 보안 컨텍스트에 저장
                //여기서 var는 컴파일러가 추론하기 때문에, 명시 생략

//                var auth = new UsernamePasswordAuthenticationToken(
//                        //여기서 null은 자격 증명(보통 비밀번호)
//                        //여기서 userDetails.getAuthorities()는 사용자의 권한 목록(빈 리스트여도 무방함)
//                        userDetailsImpl, null, userDetailsImpl.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        //다음 필터로 넘어가게 해줌 (필수!)
        filterChain.doFilter(request, response);
    }
}
