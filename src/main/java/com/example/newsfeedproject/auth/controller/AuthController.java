package com.example.newsfeedproject.auth.controller;


import com.example.newsfeedproject.auth.dto.signup.SignupRequestDto;
import com.example.newsfeedproject.auth.service.signup.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto dto){
        Long userId=signupService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공!"+userId);
    }
}
