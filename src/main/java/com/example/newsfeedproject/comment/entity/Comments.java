package com.example.newsfeedproject.comment.entity;

import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Comments")
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "contents", nullable = false, columnDefinition = "TEXT")
    private String contents;

    // User 랑 mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId", nullable = false)
    private Users userComments;

    // Feed 랑 mapping
    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "feedId", nullable = false)

    private Feeds feedComments;

    // 댓글 생성일
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 댓글 최종 수정일
    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    // 소프트 삭제 여부 (true: 삭제됨, false: 활성) - 필드명 및 컬럼명 변경

    @Builder.Default
    @Column(name="deleted", nullable = false)
    private boolean deleted = false; // <-- 기본값은 false (삭제되지 않음)

    // 댓글 내용 수정 메서드
    public void update(String contents) {
        this.contents = contents;
    }

    // 소프트 삭제 처리 메서드
    public void softDelete() {
        this.deleted = true;
    }
}
