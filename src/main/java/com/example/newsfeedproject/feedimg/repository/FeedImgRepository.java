package com.example.newsfeedproject.feedimg.repository;

import com.example.newsfeedproject.feedimg.entity.FeedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedImgRepository extends JpaRepository<FeedImage, Long> {

    // Query문으로 진행, Comments Table의 feedId로 Count
    @Query("SELECT f.feedId, i.feedImageUrl " +
            "FROM Feeds f " +                // Feeds Table as f
            "LEFT JOIN f.feedImageList i " +
            "WHERE f.feedId IN (:feedIds) ")// 연관관계 있으므로 LEFT JOIN 사용
    List<Object[]> findFeedImgByFeedId(@Param("feedIds") List<Long> feedIds);
}