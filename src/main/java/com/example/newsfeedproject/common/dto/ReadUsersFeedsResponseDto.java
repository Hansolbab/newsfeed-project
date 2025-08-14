package com.example.newsfeedproject.common.dto;


import com.example.newsfeedproject.category.entity.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReadUsersFeedsResponseDto {
    private Long feedId;
    private List<String> feedImageURL;
    private Category category;
    private String contents;
    private Integer likeTotal;
    private Integer commentTotal;
    private boolean liked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReadUsersFeedsResponseDto(Long feedId, List<String> feedImgs, String contents, Integer likeTotal, Integer commentTotal, Boolean liked) {
        this.feedId = feedId;
        this.feedImageURL = feedImgs;
        this.contents = contents;
        this.likeTotal = likeTotal;
        this.commentTotal = commentTotal;
        this.liked = liked;
    }
}
