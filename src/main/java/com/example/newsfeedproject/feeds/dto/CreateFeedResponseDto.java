package com.example.newsfeedproject.feeds.dto;

import com.example.newsfeedproject.feedimg.entity.FeedImage;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.category.entity.Category;
import com.example.newsfeedproject.common.dto.ReadUserSimpleResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class CreateFeedResponseDto {
    private Long feedId;
    private ReadUserSimpleResponseDto user;
    private String contents;
    private List<String> feedImageUrlList;
    private Category category;
    private int likeTotal = 0;
    private int commentTotal = 0;
    private boolean liked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CreateFeedResponseDto(Feeds feeds) {
        this.feedId = feeds.getFeedId();
        
        this.user = new ReadUserSimpleResponseDto(feeds.getUser().getUserName(),feeds.getUser().getProfileImageUrl(), false);
        this.contents = feeds.getContents();
        this.feedImageUrlList = feeds.getFeedImageList().stream() // Feeds 엔티티의 feedImgs에서 FeedImageUrl 추출
                .map(FeedImage::getFeedImageUrl)
                .collect(Collectors.toList());
        this.category = feeds.getCategory();
        this.liked = false;
        this.createdAt = feeds.getCreatedAt();
        this.updatedAt = feeds.getUpdatedAt();
    }
}