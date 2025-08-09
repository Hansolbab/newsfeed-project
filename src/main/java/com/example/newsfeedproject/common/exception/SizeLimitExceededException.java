package com.example.newsfeedproject.common.exception;

public class SizeLimitExceededException extends RuntimeException {
    public SizeLimitExceededException(String message) {
        super(message);
    }
}