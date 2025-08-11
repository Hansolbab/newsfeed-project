package com.example.newsfeedproject.feeds.entity;

import com.example.newsfeedproject.category.entity.Category;
import com.example.newsfeedproject.users.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @JoinColumn(name = "user_id", nullable = false) // user_id는 Users 엔티티의 userId와 매핑
    private Users user;

    // 게시글 내용 (TEXT 타입)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contents;

    // 게시글 카테고리 (Category Enum 타입으로 DB에 String 값 저장)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    // 게시글 생성일 (자동 기록)
    @CreatedDate
    @Column(updatable = false) // 생성 후 수정 불가
    private LocalDateTime createdAt;

    // 게시글 최종 수정일 (자동 기록)
    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    // Feeds 객체 생성자 (빌더 패턴)
    @Builder
    public Feeds(Users user, String contents, Category category) {
        this.user = user;
        this.contents = contents;
        this.category = category;
    }

    // 게시글 내용 및 카테고리 수정 메소드
    public void update(String contents, Category category) {
        this.contents = contents;
        this.category = category;
    }

}