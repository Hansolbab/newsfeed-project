package com.example.newsfeedproject.feeds.dto;

import com.example.newsfeedproject.feeds.entity.Feeds; // Feeds 엔티티 임포트
import com.example.newsfeedproject.category.entity.Category; // Category Enum 임포트
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FeedCreateResponseDto { // 게시글 생성 응답 데이터 전송 객체

    // 생성된 게시글 ID
    private Long feedId;

    // 게시글 작성자 정보
    private UserInfo user;

    // 게시글 내용
    private String contents;

    // 게시글 카테고리
    private Category category;

    // 게시글 생성 시간
    private LocalDateTime createdAt;

    // Feeds 엔티티로부터 응답 DTO 생성
    public FeedCreateResponseDto(Feeds feeds) {
        this.feedId = feeds.getFeedId();
        // user.getProfileImg()가 null일 경우를 대비하여 방어 코드 추가 권장
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