package com.example.newsfeedproject.common.exception;


import lombok.Getter;

//follow 관련 예외처리 해야할 모음
@Getter
public enum FollowErrorCode {

    SELF_FOLLOW_NOT(400, "자신을 팔로우 할 수 없습니다."),
    USER_NOT_FOUND(404, "해당 유저를 찾을 수 없습니다."),
    RELATION_NOT_FOUND(404, "Follow 관계를 찾을 수 없습니다."),
    ALREADY_FOLLOW(409, "이미 팔로우 관계입니다.");

    private final int status;
    private final String message;

    FollowErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
