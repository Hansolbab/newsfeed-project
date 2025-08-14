package com.example.newsfeedproject.common.exception.comment;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommentErrorCode {

    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."), // 이미 있었으면 재활용
    COMMENT_EDIT_PERMISSION_DENIED(403, "댓글 수정 권한이 없습니다."),
    COMMENT_NOT_FOUND_OR_DELETED(404, "댓글을 찾을 수 없거나 이미 삭제된 댓글입니다.");


    private final int status;
    private final String message;

    CommentErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
//new IllegalArgumentException("게시글을 찾을 수 없습니다."));
//new IllegalArgumentException("사용자를 찾을 수 없습니다."));
// new IllegalArgumentException("게시글을 찾을 수 없습니다."));
//new IllegalArgumentException("사용자를 찾을 수 없습니다."));
//new IllegalArgumentException("게시글을 찾을 수 없습니다."));
//  throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
// new IllegalArgumentException("게시글을 찾을 수 없습니다."));
//new IllegalArgumentException("사용자를 찾을 수 없습니다."));
// new IllegalArgumentException("댓글을 찾을 수 없거나 이미 삭제된 댓글입니다."));
//  throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
