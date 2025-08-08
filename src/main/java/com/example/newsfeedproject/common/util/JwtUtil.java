package com.example.newsfeedproject.common.util;



import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;


public class JwtUtil {
//유틸이란 정적, 외부에서 관리하지 않아도 되는 구조
    // 주입 필요없음, 정적메서드 위주, 빈등록 x

    private static String SECRET_KEY;
    private static Key key;

    private static final long ACCESS_EXP=1000*60*30; //30분
    private static final long REFRESH_EXP=1000*60*60*24*7; //7일
    public static void init(String secret) {
        SECRET_KEY = secret;
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /** Access Token 생성 */
    public static String createAccessToken(Long userId, String userName) {
        return Jwts.builder()
                .setSubject(userName)
                .claim("userId", userId)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXP))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Refresh Token 생성 */
    public static String createRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject("refresh")
                .claim("userId", userId)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXP))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 토큰 유효성 검사 */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /** 토큰에서 userName 추출 */
    public static String getUserNameFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /** 토큰에서 userId 추출 */
    public static Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Long.class);
    }

    public static Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
        String username = claims.getSubject();
        return new UsernamePasswordAuthenticationToken(username, null, List.of());
    }
}
