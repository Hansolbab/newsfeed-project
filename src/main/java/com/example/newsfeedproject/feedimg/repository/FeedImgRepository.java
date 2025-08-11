package com.example.newsfeedproject.feedimg.repository;

import com.example.newsfeedproject.feedimg.entity.FeedImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedImgRepository extends JpaRepository<FeedImg, Long> {
}