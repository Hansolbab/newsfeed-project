package com.example.newsfeedproject.feedimg.entity;

import com.example.newsfeedproject.feeds.entity.Feeds;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "FeedImage")
public class FeedImage {

    // 이미지 고유 ID (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedImageId;

    // 이미지가 속한 게시글 참조 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedId", nullable = false) // 외래키 컬럼명: feedId
    private Feeds feed;

    // 이미지 URL 또는 경로
    @Column(name="feedImageUrl", nullable = false, columnDefinition = "TEXT") // 컬럼명 feedImageUrl로 변경, not null, TEXT 타입
    private String feedImageUrl; // 필드명 FeedImageUrl로 변경

    // 삭제 여부
    @Column(name="deleted", nullable = false) // not null
    private boolean deleted;

    public FeedImage(String feedImageUrl, Feeds feed) {
        this.feedImageUrl = feedImageUrl;
        this.feed = feed;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

}
