package com.example.newsfeedproject.common.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowErrorResponseDto {

    private int status;
    private String message;
}
