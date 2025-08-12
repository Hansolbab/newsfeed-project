package com.example.newsfeedproject.feeds.dto;

import com.example.newsfeedproject.feedimg.entity.FeedImg;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.category.entity.Category;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CreateFeedResponseDto {

    private Long feedId;
    private UserInfo user;
    private String contents;
    private List<String> feedImageUrlList;
    private Category category;
    private int likeTotal = 0; // likeTotal 추가 (Feeds 엔티티에 없으므로 기본값 0)
    private int commentTotal = 0; // commentTotal 추가 (Feeds 엔티티에 없으므로 기본값 0)
    private boolean liked = false; // liked 추가
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CreateFeedResponseDto(Feeds feeds) {
        this.feedId = feeds.getFeedId();
        this.user = new UserInfo(feeds.getUser().getUserName(), feeds.getUser().getProfileImageUrl());
        this.contents = feeds.getContents();
        this.feedImageUrlList = feeds.getFeedImgList().stream() // Feeds 엔티티의 feedImgs에서 FeedImageUrl 추출
                .map(FeedImg::getFeedImageUrl)
                .collect(Collectors.toList());
        this.category = feeds.getCategory();
        // likeTotal, commentTotal은 Feeds 엔티티에 없으므로 DTO 생성 시 0으로 초기화
        this.liked = false;
        this.createdAt = feeds.getCreatedAt();
        this.updatedAt = feeds.getUpdatedAt();
    }

    // 작성자 정보 서브 DTO
    @Getter
    @Setter
    public static class UserInfo {
        private String userName;
        private String profileImageUrl;

        public UserInfo(String userName, String profileImageUrl) {
            this.userName = userName;
            this.profileImageUrl = profileImageUrl;
        }
    }
}