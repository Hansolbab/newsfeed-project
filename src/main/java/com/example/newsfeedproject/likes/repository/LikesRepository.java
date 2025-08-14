package com.example.newsfeedproject.likes.repository;

import com.example.newsfeedproject.likes.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

    // 특정 사용자가 특정 피드에 좋아요를 눌렀는지 확인 (liked 상태가 true인 경우만)
    Optional<Likes> findByUserIdAndFeedIdAndLikedTrue(Long userId, Long feedId);

    //너무 복잡하고 쿼리를 사용하지 않으면 메서드가 너무 길고 모든 엔티티를 돌지만 이러면, 걸러서 줍니다.
    @Query("SELECT DISTINCT  l.feedId " + // 중복되지 않는 feedId
            "FROM Likes l " + // Like 테이블에서
            "WHERE l.userId = :userId " + // 유저를 포함하고
            "AND l.liked = true " + //좋아요도 눌려 있고
            "AND l.feedId IN :feedIds") // feedIds 목록이 포함
    Set<Long> findLikedFeedIds(@Param("userId") Long userId , @Param("feedIds") Collection<Long> feedIds);

    // Query 진행, Likes Table의 feedId로 Count
    @Query("SELECT l.feedId, COUNT(l) " +
            "FROM Likes l " +                // Likes Table as l
            "WHERE l.feedId IN :feedIds AND l.liked = true " +  // feedIds 리스트 IN AND Likes Table의 liked = true 값
            "GROUP BY l.feedId")             // FeedId 별로 그룹화
    List<Object[]> countLikedByFeedIds(@Param("feedIds") List<Long> feedIds);

    @Query("SELECT l.feedId, l.liked " +
            "FROM Likes l " +                // Likes Table as l
            "WHERE l.feedId IN :feedIds AND l.userId = :userId "  // feedIds 리스트 IN AND Likes Table의 liked = true 값
            )
    List<Object []> isLikedByFeedIdsANDUserId(@Param("feedIds") List<Long> feedIds, @Param("userId") Long userId);

    Optional<Likes> findByUserIdAndFeedId(Long userId, Long feedId);

    Long countByFeedIdAndLikedTrue(Long feedId);


    @Query("SELECT l.feedId FROM  Likes l WHERE  l.userId = :meId AND l.liked = true")
    Set<Long> findLikesByFeedId(@Param(("meId")) Long meId);

    // feedId, likeTotal, user liked 확인
    @Query("SELECT l.feedId, " +
            "COUNT(CASE WHEN l.feedId IN :feedIds AND l.liked = true THEN 1 END ), " +
            "COUNT(CASE WHEN l.feedId IN :feedIds AND l.userId = :userId THEN 1 END ) " +
            "FROM Likes l " +
            "WHERE l.feedId IN :feedIds " +
            "GROUP BY l.feedId")
    List<Object []> countLikesAndIsLikedByFeedIds(@Param("feedIds") List<Long> feedIds, @Param("userId") Long userId);
}