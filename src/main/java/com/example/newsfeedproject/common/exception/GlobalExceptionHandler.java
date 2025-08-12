package com.example.newsfeedproject.common.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


        @ExceptionHandler(FollowErrorException.class)
        public ResponseEntity<FollowErrorResponseDto> handleFollowError(FollowErrorException e) {

            FollowErrorCode errorCode = e.getFollowErrorCode();

            return ResponseEntity.status(errorCode.getStatus()).body(
                    new FollowErrorResponseDto(errorCode.getStatus(), errorCode.getMessage()));
        }
}
