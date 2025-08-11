package com.example.newsfeedproject.feeds.dto;

import com.example.newsfeedproject.feeds.entity.Feeds; // Feeds 엔티티 임포트
import com.example.newsfeedproject.category.entity.Category; // Category Enum 임포트
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FeedResponseDto { // 게시글 조회/응답 데이터 전송 객체

    // 게시글 고유 ID
    private Long feedId;

    // 게시글 작성자 정보
    private UserInfo user;

    // 게시글 내용
    private String contents;

    // 게시글 카테고리
    private Category category;

    // 게시글 생성 시간
    private LocalDateTime createdAt;

    // 게시글 최종 수정 시간
    private LocalDateTime updatedAt;

    // Feeds 엔티티로부터 DTO 생성 (isLiked, likeTotal, commentTotal 관련 파라미터 및 필드 제거)
    public FeedResponseDto(Feeds feeds) { // 생성자에서 liked 파라미터 제거
        this.feedId = feeds.getFeedId();
        // User 엔티티에 profileImg 게터 필요
        this.user = new UserInfo(feeds.getUser().getUserName(), feeds.getUser().getProfileImg());
        this.contents = feeds.getContents();
        this.category = feeds.getCategory();
        // likeTotal, commentTotal 필드 제거
        // isLiked 필드 제거
        this.createdAt = feeds.getCreatedAt();
        this.updatedAt = feeds.getUpdatedAt();
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