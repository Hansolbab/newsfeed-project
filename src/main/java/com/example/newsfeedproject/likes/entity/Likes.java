package com.example.newsfeedproject.likes.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "likes")
public class Likes {

    // 좋아요 고유 ID (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    // 좋아요를 누른 사용자 ID
    @Column(name="userId", nullable = false)
    private Long userId;

    // 좋아요를 받은 게시글 ID
    @Column(name="feedId", nullable = false)
    private Long feedId;

    // 좋아요 상태 (true: 좋아요, false: 좋아요 아님)
    @Column(name="liked", nullable = false)
    private boolean liked;

    public Likes (Long userId, Long feedId, boolean liked) {
        this.userId = userId;
        this.feedId = feedId;
        this.liked = liked;
    }

    public void setLiked(boolean liked){
        this.liked = liked;
    }
}