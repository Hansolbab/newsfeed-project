package com.example.newsfeedproject.common.exception.auth;

import com.example.newsfeedproject.common.exception.ErrorException;
import lombok.Getter;

@Getter
public class AuthErrorException extends ErrorException {

    private final AuthErrorCode authErrorCode;

    public AuthErrorException(AuthErrorCode authErrorCode) {
        super(authErrorCode.getMessage(), authErrorCode.getStatus());
        this.authErrorCode = authErrorCode;
    }
}
