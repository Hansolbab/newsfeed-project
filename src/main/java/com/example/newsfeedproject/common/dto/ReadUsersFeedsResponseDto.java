package com.example.newsfeedproject.common.dto;


import lombok.Getter;

import java.util.List;

@Getter
public class ReadUsersFeedsResponseDto {

    private Long feedId;
    private List<String> feedImageURL;
    private String contents;
    private Integer likeTotal;
    private Integer commentTotal;
    private boolean liked;


    public ReadUsersFeedsResponseDto(Long feedId, List<String> feedImgs, String contents, Integer likeTotal, Integer commentTotal, Boolean liked) {
        this.feedId = feedId;
        this.feedImageURL = feedImgs;
        this.contents = contents;
        this.likeTotal = likeTotal;
        this.commentTotal = commentTotal;
        this.liked = liked;
    }
}
