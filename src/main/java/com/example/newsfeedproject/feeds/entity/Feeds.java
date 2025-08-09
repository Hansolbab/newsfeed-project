package com.example.newsfeedproject.feeds.entity;

import com.example.newsfeedproject.category.entity.Category; // Category Enum 임포트
import com.example.newsfeedproject.users.entity.Users; // Users 엔티티 임포트
import jakarta.persistence.*;
import lombok.AccessLevel; // Lombok AccessLevel 임포트
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Setter를 필요한 경우에 사용
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter // 엔티티는 Getter만 사용하는 것을 권장
@Setter // 필요에 따라 Setter 사용 (Builder로 대체 가능)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자, 외부 직접 생성 방지
@Entity
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
@Table(name = "feeds") // 실제 DB 테이블명 지정
public class Feeds { // 게시글 엔티티

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

    // 좋아요 총 개수
    private int likeTotal;

    // 댓글 총 개수
    private int commentTotal;

    // 게시글 생성일 (자동 기록)
    @CreatedDate
    @Column(updatable = false) // 생성 후 수정 불가
    private LocalDateTime createdAt;

    // 게시글 최종 수정일 (자동 기록)
    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    // 소프트 삭제 여부 (true: 삭제됨, false: 활성)
    private Boolean deleted;

    // Feeds 객체 생성자 (빌더 패턴)
    @Builder
    public Feeds(Users user, String contents, Category category) {
        this.user = user;
        this.contents = contents;
        this.category = category;
        this.likeTotal = 0; // 초기값 설정
        this.commentTotal = 0; // 초기값 설정
        this.deleted = false; // 기본값은 '삭제되지 않음'
    }

    // 게시글 내용 및 카테고리 수정 메소드
    public void update(String contents, Category category) {
        this.contents = contents;
        this.category = category;
    }

    // 좋아요 수, 댓글 수 변경 메소드 (나중에 좋아요/댓글 기능 구현 시 필요)
    public void incrementLikeTotal() {
        this.likeTotal++;
    }

    public void decrementLikeTotal() {
        if (this.likeTotal > 0) {
            this.likeTotal--;
        }
    }

    public void incrementCommentTotal() {
        this.commentTotal++;
    }

    public void decrementCommentTotal() {
        if (this.commentTotal > 0) {
            this.commentTotal--;
        }
    }

    // 소프트 삭제 처리
    public void softDelete() {
        this.deleted = true;
    }
}