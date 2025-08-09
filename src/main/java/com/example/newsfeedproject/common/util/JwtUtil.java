package com.example.newsfeedproject.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.auth.impl.UserDetailsServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.List;
//static 메서드만 제공되며, 빈 등록 없이 사용
public class JwtUtil {
    //서명(암호화) 알고리즘 객체 (HMAC256)
    private static Algorithm algorithm;
    // 토큰 검증기: 이 알고리즘으로 서명된 토큰인지 검사
    private static JWTVerifier verifier;

    private static final long ACCESS_EXP  = 1000L * 60 * 30; //30분
    private static final long REFRESH_EXP = 1000L * 60 * 60 * 24 * 7; //7일

    // secret 암호화할 때 쓰는 비밀번호 같은 문자열
    public static void init(String secret) {
        //HMAC256: 위조할 수 없도록 서명(Signature)할 때 쓰는 알고리즘
        algorithm = Algorithm.HMAC256(secret);
        //해당 알고리즘으로 생성된 토큰만 허용하도록 검증기 생성
        verifier  = JWT.require(algorithm).build();
    }


    //Access  Token 생성
    public static String createAccessToken(Long userId, String email) {
        return JWT.create()
                .withSubject(email)
                .withClaim("userId", userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_EXP))
                .sign(algorithm);
    }

    // Refresh Token 생성
    public static String createRefreshToken(Long userId) {
        return JWT.create()
                .withSubject("refresh")
                .withClaim("userId", userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_EXP))
                .sign(algorithm);
    }

    // 토큰 유효성 검사
    public static boolean validateToken(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    //토큰 디코드
    private static DecodedJWT decode(String token) {
        return verifier.verify(token);
    }

    // 토큰에서 email(subject) 추출
    public static String getUserEmailFromToken(String token) {
        return decode(token).getSubject();
    }

    //토큰에서 userId 클레임(Long) 추출
    public static Long getUserIdFromToken(String token) {
        return decode(token).getClaim("userId").asLong();
    }

    //Spring Security Authentication 객체 생성(권한은 빈 리스트)
//    public static Authentication getAuthentication(String token) {
//        String email = getUserEmailFromToken(token);
//        return new UsernamePasswordAuthenticationToken(email, null, List.of());
//    }
    public static Authentication getAuthentication(String token, UserDetailsServiceImpl userDetailsServiceImpl) {
        String email = getUserEmailFromToken(token);
        UserDetailsImpl userDetails = userDetailsServiceImpl.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
    }
}
