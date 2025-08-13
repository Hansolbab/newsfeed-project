package com.example.newsfeedproject.comment.repository;

import com.example.newsfeedproject.comment.entity.Comments;
import com.example.newsfeedproject.feeds.entity.Feeds;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {
    // 특정 Feed에 속한 댓글들을 조회하는 쿼리 메서드 (최신순 정렬)
    @Query("SELECT c FROM Comments c WHERE c.feedComments.feedId = :feedId AND c.deleted = false ORDER BY c.createdAt DESC")
    Page<Comments> findByFeedComments(@Param("feedId") Long feedId, Pageable pageable);

    // 특정 Feed에 속한 특정 댓글 조회 (deleted가 아닌 경우)
    @Query("SELECT c FROM Comments c WHERE c.commentId = :commentId AND c.feedComments.feedId = :feedId AND c.deleted = false")
    Optional<Comments> findByCommentIdAndFeedComments(@Param("commentId") Long commentId, @Param("feedId") Long feedId);
    Optional<Comments> findByCommentIdAndFeedComments(@Param("commentId") Long commentId, @Param("feed") Feeds feed);

    // 사용자ID와 FeedID로 댓글 조회 (작성자 확인 및 삭제 여부 확인 시 유용)
    @Query("SELECT c FROM Comments c " +
            "WHERE c.commentId = :commentId " +
            "AND c.userComments.userId = :userId " +
            "AND c.feedComments.feedId = :feedId " +
            "AND c.deleted = false")
    Optional<Comments> findByCommentIdAndUserIdAndFeedId(
            @Param("commentId") Long commentId,
            @Param("userId") Long userId,
            @Param("feedId") Long feedId);

    // Query문으로 진행, Comments Table의 feedId로 Count
    @Query("SELECT f.feedId, COUNT(c) " +
            "FROM Feeds f " +                // Feeds Table as f
            "LEFT JOIN f.comments c " +      // 연관관계 있으므로 LEFT JOIN 사용
            "WHERE f.feedId IN (:feedIds) AND c.contents IS NOT NULL " +
            "GROUP BY f.feedId")
    List<Object[]> countCommentsByFeedIds(@Param("feedIds") List<Long> feedIds);

}
