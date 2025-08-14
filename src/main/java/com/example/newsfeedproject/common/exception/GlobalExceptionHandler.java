package com.example.newsfeedproject.common.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleFollowError(ErrorException e) {

        return ResponseEntity.status(e.getStatus()).body(
                new ErrorResponseDto(e.getStatus() //HttpStatus
                        , e.getMessage()));
    }

}
