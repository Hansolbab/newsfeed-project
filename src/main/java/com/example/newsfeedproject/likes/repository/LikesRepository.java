package com.example.newsfeedproject.likes.repository;

import com.example.newsfeedproject.likes.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

    // 특정 사용자가 특정 피드에 좋아요를 눌렀는지 확인 (liked 상태가 true인 경우만)
    Optional<Likes> findByUserIdAndFeedIdAndLikedTrue(Long userId, Long feedId);

    // Query문으로 진행, Likes Table의 feedId로 Count
    @Query("SELECT l.feedId, COUNT(l) " +
           "FROM Likes l " +                // Likes Table as l
           "WHERE l.feedId IN :feedIds AND l.liked = true " +  // feedIds 리스트 IN AND Likes Table의 liked = true 값
           "GROUP BY l.feedId")             // FeedId 별로 그룹화
    List<Object[]> countLikedByFeedIds(@Param("feedIds") List<Long> feedIds);

    @Query("SELECT l.feedId " +
            "FROM Likes l " +                // Likes Table as l
            "WHERE l.feedId IN :feedIds AND l.userId = :userId AND l.liked = true " +  // feedIds 리스트 IN AND Likes Table의 liked = true 값
            "GROUP BY l.feedId")
    List<Object []> isLikedByFeedIdsANDUserId(@Param("feedIds") List<Long> feedIds, Long userId);
}