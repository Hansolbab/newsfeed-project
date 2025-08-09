package com.example.newsfeedproject.like.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; // AllArgsConstructor 추가 (Builder 사용 시 필요)

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자
@AllArgsConstructor // Builder 사용 시 모든 필드를 포함하는 생성자 필요
@Builder // Builder 패턴
@Entity
@Table(name = "likes") // 실제 DB 테이블명 지정
public class Like { // 좋아요 엔티티

    // 좋아요 고유 ID (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MySQL에 적합한 전략
    private Long likeId;

    // 좋아요를 누른 사용자 ID
    @Column(name="userId", nullable = false)
    private Long userId;

    // 좋아요를 받은 게시글 ID
    @Column(name="feedId", nullable = false)
    private Long feedId;

    // 좋아요 상태 (true: 좋아요, false: 좋아요 아님 - 팀 협의 내용)
    @Column(name="liked", nullable = false)
    private boolean liked; // 필드명 'liked' 소문자로 변경

    // 추후 created_at, updated_at 필요 시 추가
    // @CreatedDate @Column(updatable = false) private LocalDateTime createdAt;
    // @LastModifiedDate @Column private LocalDateTime updatedAt;
}