package com.example.newsfeedproject.auth.dto.signin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SigninResponseDto {
    private String accessToken;
    private String refreshToken;
}
