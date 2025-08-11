package com.example.newsfeedproject.feeds.dto;

import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.category.entity.Category;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateFeedResponseDto {

    private Long feedId;

    private UserInfo user;

    private String contents;

    private Category category;

    private LocalDateTime createdAt;

    // Feeds 엔티티로부터 응답 DTO 생성
    public CreateFeedResponseDto(Feeds feeds) {
        this.feedId = feeds.getFeedId();
        this.user = new UserInfo(feeds.getUser().getUserName(), feeds.getUser().getProfileImg());
        this.contents = feeds.getContents();
        this.category = feeds.getCategory();
        this.createdAt = feeds.getCreatedAt();
    }

    // 작성자 정보 서브 DTO
    @Getter
    @Setter
    public static class UserInfo {
        private String userName;
        private String profileImg;

        public UserInfo(String userName, String profileImg) {
            this.userName = userName;
            this.profileImg = profileImg;
        }
    }
}