package com.example.newsfeedproject.auth.controller;


import com.example.newsfeedproject.auth.dto.signin.SigninRequestDto;
import com.example.newsfeedproject.auth.dto.signin.SigninResponseDto;
import com.example.newsfeedproject.auth.dto.signup.SignupRequestDto;
import com.example.newsfeedproject.auth.service.signin.SigninService;
import com.example.newsfeedproject.auth.service.signup.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final SignupService signupService;
    private final SigninService signinService;
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto dto){
        Long userId=signupService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공!"+userId);
    }

    //로그인은 200OK
    @PostMapping("/signin")
    public ResponseEntity<SigninResponseDto> signin(@Valid @RequestBody SigninRequestDto dto) {
        SigninResponseDto tokens = signinService.signin(dto);

        //리프레시 토큰을 HttpOnly 쿠키로 보내기
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/api/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity.ok()
                // 여기서 쿠키로 내려보냄
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                //바디에는 access만 내려도 됨(refresh는 쿠키로 보냈으니까 null)
//                .body(new SigninResponseDto(tokens.getAccessToken(), null));
        
                // (개발용) 바디에도 같이 넣기
                .body(new SigninResponseDto(tokens.getAccessToken(), tokens.getRefreshToken()));
    }
    //TODO: 로그아웃
    @PostMapping("/signout")
    public ResponseEntity<Void> signout() {
        // refreshToken= : 값 비움
        //Max-Age=0 + Expires=Thu, 01 Jan 1970…: 즉시 만료
        //Path=/api/auth/refresh: 쿠키만 삭제
        // HttpOnly; SameSite=Strict: 아래 옵션 맞춰서 제거
        ResponseCookie deleteCookie =ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/api/auth/refresh")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

}
