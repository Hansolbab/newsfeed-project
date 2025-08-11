package com.example.newsfeedproject.likes.repository;

import com.example.newsfeedproject.likes.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

    // 특정 사용자가 특정 피드에 좋아요를 눌렀는지 확인 (liked 상태가 true인 경우만)
    Optional<Likes> findByUserIdAndFeedIdAndLikedTrue(Long userId, Long feedId);

}