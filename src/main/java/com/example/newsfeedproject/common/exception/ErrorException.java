package com.example.newsfeedproject.common.exception;


import lombok.Getter;

// 공통 추상화
@Getter
public abstract class ErrorException extends RuntimeException {

    private final String message;
    private final int status;

    public ErrorException(String message, int status) {
        this.message=message;
        this.status = status;
    }

}
