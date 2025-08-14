package com.example.newsfeedproject.common.exception.users;

import lombok.Getter;

@Getter
//TODO: 마이인포의 전화번호, 프로필사진
public enum UsersErrorCode {
    NOT_A_USER(400, "유저가 아닙니다."),
    LOGIN_REQUIRED(401, "로그인이 필요합니다."),
    NO_SUCH_USER(404, "없는 유저입니다."),
    FOLLOW_REQUIRED(400, "팔로우 필요"),

    USER_NOT_EXIST(404, "존재하지 않는 사용자입니다."),
    CURRENT_USER_NOT_FOUND(404, "현재 사용자를 찾을 수 없습니다."),

    NOT_POST_AUTHOR(403, "작성자가 아닙니다."),//게시글 수정, 삭제 권한 , 댓글 수정, 삭제
    USER_IS_PRIVATE(403,"비공개 유저입니다.");//비공개유저저

    private final int status;
    private final String message;

    UsersErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}


