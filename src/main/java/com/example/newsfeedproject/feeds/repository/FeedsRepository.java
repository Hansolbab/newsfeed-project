package com.example.newsfeedproject.feeds.repository;

import com.example.newsfeedproject.feeds.entity.Feeds;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedsRepository extends JpaRepository<Feeds, Long> {
    Page<Feeds> findAllByOrderByFeedIdDesc(Pageable pageable);
}