package com.example.newsfeedproject.feeds.entity;

import com.example.newsfeedproject.category.entity.Category;
import com.example.newsfeedproject.comment.entity.Comments;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.feedimg.entity.FeedImage;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "feeds")
public class Feeds {

    // 게시글 고유 ID (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedId;

    // 게시글 작성자 참조 (Users 엔티티와의 N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false) // userId는 Users 엔티티의 userId와 매핑
    private Users user;

    // 게시글 내용 (TEXT 타입)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contents;

    // 게시글 카테고리 (Category Enum 타입으로 DB에 String 값 저장)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    // 게시글 이미지 목록 (FeedImg 엔티티와의 1:N 관계, cascade = ALL로 연관 작업 자동화)
    @Builder.Default
    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedImage> feedImageList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "feedComments", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comments> comments = new ArrayList<>();

    // 게시글 생성일 (자동 기록)
    @CreatedDate
    @Column(updatable = false) // 생성 후 수정 불가
    private LocalDateTime createdAt;

    // 게시글 최종 수정일 (자동 기록)
    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    @Builder.Default // 빌더 사용 시 기본값 설정
    @Setter
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;


    // 게시글 내용 및 카테고리 수정 메소드
    public void update(String contents, Category category) {
        this.contents = contents;
        this.category = category;
    }

    // 소프트 삭제 처리 메서드 추가
    public void softDelete() {
        this.deleted = true;
    }

    public void restore() {
        this.deleted = false;
    }

    // (참고: likeTotal, commentTotal 관련 메소드는 엔티티에 필드가 없으므로 포함하지 않음)
}