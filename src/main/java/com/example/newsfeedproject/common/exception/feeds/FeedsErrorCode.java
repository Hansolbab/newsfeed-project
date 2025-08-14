package com.example.newsfeedproject.common.exception.feeds;

import lombok.Getter;

@Getter
// TODO: 카테고리, 라이크 같이 넣기
public enum FeedsErrorCode {
    // 피드/게시글 관련
//    FEED_NOT_FOUND(404, "피드가 존재하지 않습니다."),// 주석: "피드 없음"
    POST_NOT_FOUND(404, "게시글을 찾을 수 없습니다."), //재혁님 코드, 지훈님 코드 공용."
    USER_NOT_FOUND_CURRENT(404, "현재 사용자를 찾을 수 없습니다."),// 주석: "현재 사용자를 찾을 수 없습니다."     // 주석: "게시글 삭제 권한이 없습니다."
    NOT_FOUND_RESET_POST(404, "복구할 게시글이 없습니다."),
    NOT_FOUND_CATEGORY(404, "존재하지 않는 카테고리입니다.");
    private final int status;
    private final String message;

    FeedsErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
