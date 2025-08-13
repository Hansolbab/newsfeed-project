package com.example.newsfeedproject.auth.controller;

import com.example.newsfeedproject.auth.dto.signin.SignInRequestDto;
import com.example.newsfeedproject.auth.dto.signin.SignInResponseDto;
import com.example.newsfeedproject.auth.dto.signup.SignUpRequestDto;
import com.example.newsfeedproject.auth.service.signin.SignInService;
import com.example.newsfeedproject.auth.service.signup.SignUpService;
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
    private final SignUpService signUpService;
    private final SignInService signInService;

    // 회원가입
    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto){
        Long userId = signUpService.signUp(signUpRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공!"+userId);
    }

    //로그인
    @PostMapping("/signIn")
    public ResponseEntity<SignInResponseDto> signIn(@Valid @RequestBody SignInRequestDto signInRequestDto) {
        SignInResponseDto tokens = signInService.signIn(signInRequestDto);

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
                //바디에는 access만 내려도 됨(refresh는 쿠키로 보냈으니까 null -> 세션으로 보내주세요)
                .body(new SignInResponseDto(tokens.getAccessToken(), null));
    }

    // 로그아웃
    @PostMapping("/signOut")
    public ResponseEntity<Void> signOut() {
        ResponseCookie deleteCookie =ResponseCookie.from("refreshToken", "")// refreshToken= : 값 비움
                .httpOnly(true)// HttpOnly; SameSite=Strict: 아래 옵션 맞춰서 제거
                .secure(false)
                .sameSite("Strict")
                .path("/api/auth/refresh")//Path=/api/auth/refresh: 쿠키만 삭제
                .maxAge(0)//Max-Age=0 + Expires=Thu, 01 Jan 1970…: 즉시 만료
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

}
