package com.example.newsfeedproject.feeds.repository;

import com.example.newsfeedproject.feeds.entity.Feeds; // Feeds 엔티티 임포트
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Feeds 엔티티 데이터 접근
@Repository
public interface FeedsRepository extends JpaRepository<Feeds, Long> {
    // 필요한 사용자 정의 쿼리 메소드 추가 가능
    Page<Feeds> findByUser_UserId(Long userId, Pageable pageable);
}