package com.example.newsfeedproject.auth.util;


import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.List;

@Component
public class JwtUtil {

//    private  final Key secretKey=Keys.hmacShaKeyFor(
//            "비밀키 32바이트 이상인 랜덤문자열이어야 합니다.",getBytes()
//    );
    //토큰 만료 30분
    private final long validityMs = 1000 * 60 * 30;
    //토큰 유효성 생성
//    public String createAccessToken(String userId) {
//        Date now = new Date();
//        return Jwts.builder()
//                .setSubject(userId)
//                .setIssuedAt(now)
//                .setExpiration(new Date(now.getTime() + validityMs))
//                .signWith(secretKey, SignatureAlgorithm.HS256)
//                .compact();
//    }
    //토큰 유효성 검사
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(secretKey)
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (JwtException | IllegalArgumentException e) {
//            return false;
//        }
//    }

    //토큰에서 Authentication 객체 추출(userId 담아서 반환)
//    public Authentication getAuthentication(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(secretKey)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        String userId = claims.getSubject();
//        // 실제 구현 시 UserDetailsService를 통해 사용자 정보와 권한을 불러와야 합니다.
//        return new UsernamePasswordAuthenticationToken(userId, null, List.of());
//    }
}
