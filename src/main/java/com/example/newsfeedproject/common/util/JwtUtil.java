package com.example.newsfeedproject.common.util;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtUtil {

//    private  final Key secretKey=Keys.hmacShaKeyFor(
//            "비밀키 32바이트 이상인 랜덤문자열이어야 합니다.",getBytes()
//    );
    //토큰 만료 30분
//    private final long validityMs = 1000 * 60 * 30;

    public boolean validateToken(String token) {
        return true;
    }
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        // 그냥 dummy user 로 통과시켜 줌
        return new UsernamePasswordAuthenticationToken("dummyUser", null, List.of());
    }
}
