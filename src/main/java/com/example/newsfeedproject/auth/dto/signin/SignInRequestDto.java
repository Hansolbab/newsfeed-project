package com.example.newsfeedproject.auth.dto.signin;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SigninRequestDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
