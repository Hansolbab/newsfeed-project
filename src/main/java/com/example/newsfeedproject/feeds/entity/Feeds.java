package com.example.newsfeedproject.feeds.entity;

import com.example.newsfeedproject.category.entity.CategoryEntity;
import com.example.newsfeedproject.feedImg.FeedImg;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.newsfeedproject.category.entity.Category; // Category enum 임포트
import com.example.newsfeedproject.users.entity.Users; // Users 엔티티 임포트 (User에서 Users로 변경)

@Entity
@Table(name = "feeds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feeds {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // user_id는 Users 엔티티의 userId와 매핑
    private Users user; // Users 타입으로 변경

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contents;

    @OneToOne(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private FeedImg feedImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = true)
    private CategoryEntity categoryEntity;

    @Transient
    private Boolean deleted;
}