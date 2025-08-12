package com.example.newsfeedproject.feedimg.repository;

import com.example.newsfeedproject.feedimg.entity.FeedImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FeedImgRepository extends JpaRepository<FeedImg, Long> {


    // Query문으로 진행, Comments Table의 feedId로 Count
    @Query("SELECT fi.feed.feedId, fi.feedImageUrl " +
            "FROM FeedImg fi " +
            "WHERE fi.feed.feedId IN :feedIds")
    List<Object[]> findFeedImgByFeedId(@Param("feedIds") List<Long> feedIds);

}