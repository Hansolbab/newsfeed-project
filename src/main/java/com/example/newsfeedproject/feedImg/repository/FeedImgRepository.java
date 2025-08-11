package com.example.newsfeedproject.feedImg.repository;

import com.example.newsfeedproject.feedImg.entity.FeedImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedImgRepository extends JpaRepository<FeedImg, Long> {
}
