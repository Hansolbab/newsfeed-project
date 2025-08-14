package com.example.newsfeedproject.feeds.dto;

import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.category.entity.Category;
import com.example.newsfeedproject.feedimg.entity.FeedImage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class FeedsResponseDto {

    // 게시글 고유 ID
    private Long feedId;

    // 게시글 작성자 정보
    private UserInfo user;

    // 게시글 내용
    private String contents;

    // 게시글 이미지 URL 목록
    private List<String> feedImageUrlList;

    // 게시글 카테고리
    private Category category;

    // 좋아요 총 개수
    private int likeTotal = 0; // Feeds 엔티티에 없으므로 DTO에서 0으로 초기화

    // 댓글 총 개수
    private int commentTotal = 0; // Feeds 엔티티에 없으므로 DTO에서 0으로 초기화

    // 현재 사용자가 좋아요했는지 여부
    private boolean liked;

    // 게시글 생성 시간
    private LocalDateTime createdAt;

    // 게시글 최종 수정 시간
    private LocalDateTime updatedAt;

    // Feeds 엔티티와 'liked' 여부로부터 DTO 생성
    public FeedsResponseDto(Feeds feeds, boolean liked) {
        this.feedId = feeds.getFeedId();
        this.user = new UserInfo(feeds.getUser().getUserName(), feeds.getUser().getProfileImageUrl());
        this.contents = feeds.getContents();
        this.feedImageUrlList = feeds.getFeedImageList().stream() // Feeds 엔티티의 feedImgs에서 imageUrl 추출
                .map(FeedImage::getFeedImageUrl)
                .collect(Collectors.toList());
        this.category = feeds.getCategory(); // Feeds 엔티티에서 가져옴
        // likeTotal, commentTotal은 Feeds 엔티티에 없으므로 DTO에서 0으로 초기화
//        this.likeTotal = likeTotal; //
        this.liked = liked;
        this.createdAt = feeds.getCreatedAt();
        this.updatedAt = feeds.getUpdatedAt();
    }
    public FeedsResponseDto(Feeds feeds, boolean liked ,int likeTotal, int commentTotal) {
        this.feedId = feeds.getFeedId();
        this.user = new UserInfo(feeds.getUser().getUserName(), feeds.getUser().getProfileImageUrl());
        this.contents = feeds.getContents();
        this.feedImageUrlList = feeds.getFeedImageList().stream() // Feeds 엔티티의 feedImgs에서 imageUrl 추출
                .map(FeedImage::getFeedImageUrl)
                .collect(Collectors.toList());
        this.category = feeds.getCategory(); // Feeds 엔티티에서 가져옴
        this.likeTotal = likeTotal; //
        this.commentTotal = commentTotal;
        this.liked = liked;
        this.createdAt = feeds.getCreatedAt();
        this.updatedAt = feeds.getUpdatedAt();
    }


    public static FeedsResponseDto toDto(Feeds feeds, boolean liked, int likeTotal, int commentTotal) {
        return  new FeedsResponseDto( feeds , liked , likeTotal, commentTotal);
    }

    


    // 작성자 정보 서브 DTO (변동 없음)
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