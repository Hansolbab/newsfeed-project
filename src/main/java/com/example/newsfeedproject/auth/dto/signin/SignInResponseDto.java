package com.example.newsfeedproject.auth.dto.signin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInResponseDto {
    private String accessToken;
    private String refreshToken;
}
