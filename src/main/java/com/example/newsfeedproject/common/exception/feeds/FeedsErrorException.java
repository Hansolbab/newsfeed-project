package com.example.newsfeedproject.common.exception.feeds;

import com.example.newsfeedproject.common.exception.ErrorException;
import com.example.newsfeedproject.common.exception.comment.CommentErrorCode;
import lombok.Getter;

@Getter
public class FeedsErrorException extends ErrorException {

    private final FeedsErrorCode feedsErrorCode;

    public FeedsErrorException(FeedsErrorCode feedsErrorCode) {
        super(feedsErrorCode.getMessage() , feedsErrorCode.getStatus());
        this.feedsErrorCode = feedsErrorCode;
    }
}
