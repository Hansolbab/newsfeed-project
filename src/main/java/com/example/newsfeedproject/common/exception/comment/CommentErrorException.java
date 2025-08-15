package com.example.newsfeedproject.common.exception.comment;

import com.example.newsfeedproject.common.exception.ErrorException;
import com.example.newsfeedproject.common.exception.feeds.FeedsErrorCode;
import lombok.Getter;

@Getter
public class CommentErrorException extends ErrorException {

    private final CommentErrorCode commentErrorCode;

    public CommentErrorException(CommentErrorCode commentErrorCode) {
        super(commentErrorCode.getMessage() , commentErrorCode.getStatus());
        this.commentErrorCode = commentErrorCode;
    }
}
