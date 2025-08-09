package com.example.newsfeedproject.like.repository;

import com.example.newsfeedproject.like.entity.Like; // Like 엔티티 임포트
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    // 특정 사용자가 특정 피드에 좋아요를 눌렀는지 확인 (liked 상태가 true인 경우만)
    Optional<Like> findByUserIdAndFeedIdAndLikedTrue(Long userId, Long feedId);

    // 특정 사용자가 특정 피드에 좋아요를 누른 기록이 있는지 확인 (liked 상태와 무관하게)
    Optional<Like> findByUserIdAndFeedId(Long userId, Long feedId);

    // 특정 게시글의 좋아요 수 카운트 (추가 필요 시)
    long countByFeedIdAndLikedTrue(Long feedId);
}