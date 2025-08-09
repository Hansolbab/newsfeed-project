package com.example.newsfeedproject.feeds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedResponseDto {
    private Long feedId;
    private UserResponseDto userName; // Users 엔티티의 정보
    private String contents;
    private String feedImgs;
    private String category;
    private Integer likeTotal;
    private Integer commentTotal;
    private Boolean isLiked;
}