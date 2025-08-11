package com.example.newsfeedproject.comment.repository;

import com.example.newsfeedproject.comment.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {


    // Query문으로 진행, Comments Table의 feedId로 Count
    @Query("SELECT f.feedId, COUNT(c) " +
           "FROM Feeds f " +                // Feeds Table as f
           "LEFT JOIN f.comments c " +      // 연관관계 있으므로 LEFT JOIN 사용
           "WHERE f.feedId IN (:feedIds) AND c.contents IS NOT NULL " +
           "GROUP BY f.feedId")
    List<Object[]> countCommentsByFeedIds(@Param("feedIds") List<Long> feedIds);
}
