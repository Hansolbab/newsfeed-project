package com.example.newsfeedproject.common.exception.follow;

import com.example.newsfeedproject.common.exception.ErrorException;
import lombok.Getter;

@Getter
public class FollowErrorException extends ErrorException {

    private final FollowErrorCode followErrorCode;

    public FollowErrorException(FollowErrorCode followErrorCode) {
        super(followErrorCode.getMessage() , followErrorCode.getStatus());
        this.followErrorCode = followErrorCode;
    }
}
