package com.example.newsfeedproject.feedimg.entity;

import com.example.newsfeedproject.feeds.entity.Feeds;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "FeedImg")
public class FeedImg {

    // 이미지 고유 ID (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedImgId;

    // 이미지가 속한 게시글 참조 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false) // 외래키 컬럼명: feed_id
    private Feeds feed;

    // 이미지 URL 또는 경로
    @Column(name="image_url", nullable = false, columnDefinition = "TEXT") // 컬럼명 image_url로 변경, not null, TEXT 타입
    private String imageUrl; // 필드명 imageUrl로 변경

    // 삭제 여부
    @Column(name="deleted", nullable = false) // not null
    private boolean deleted;
}