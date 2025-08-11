package com.example.newsfeedproject.likes.repository;

import com.example.newsfeedproject.likes.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

    // 특정 사용자가 특정 피드에 좋아요를 눌렀는지 확인 (liked 상태가 true인 경우만)
    Optional<Likes> findByUserIdAndFeedIdAndLikedTrue(Long userId, Long feedId);

    //너무 복잡하고 쿼리를 사용하지 않으면 메서드가 너무 길고 모든 엔티티를 돌지만 이러면, 걸러서 줍니다.
    @Query("select distinct  l.feedId " + // 중복되지 않는 feedId
            "from Likes l" + // Like 테이블에서
            " where l.userId = :userId " + // 유저를 포함하고
            "and l.liked = true " + //좋아요도 눌려 있고
            "and l.feedId in :feedIds") // feedIds 목록이 포함
    Set<Long> findLikedFeedIds(@Param("userId") Long userId , @Param("feedIds") Collection<Long> feedIds);
}