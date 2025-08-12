package com.example.newsfeedproject.common.exception;

import lombok.Getter;

@Getter
public class FollowErrorException extends  RuntimeException{

    private final FollowErrorCode followErrorCode;


    public FollowErrorException(FollowErrorCode followErrorCode) {
        super(followErrorCode.getMessage());
        this.followErrorCode = followErrorCode;
    }
}
