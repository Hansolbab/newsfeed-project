package com.example.newsfeedproject.feeds.repository;

import com.example.newsfeedproject.category.entity.Category;
import com.example.newsfeedproject.feeds.entity.Feeds; // Feeds 엔티티 임포트
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


// Feeds 엔티티 데이터 접근
@Repository
public interface FeedsRepository extends JpaRepository<Feeds, Long> {


    Page<Feeds> findByCategory(Category category, Pageable pageable);
    // 필요한 사용자 정의 쿼리 메소드 추가 가능
    Page<Feeds> findByUser_UserId(Long userId, Pageable pageable);


    @Query(value = "select f from Feeds f" + // 실제 데이터 조회용
            " join f.comments c" + // Feeds에 매핑된 comments 컬렉션을 들고 온다.
            " where  c.userComments.userId = :meId" + // 유저 아이디가 나
            " group by  f.feedId " + // 내가 단 여러개의 댓글을 그룹화
            "order by max(c.createdAt) desc", // 내 댓글 기준으로 최신순
        countQuery = "select count(distinct f.feedId)" + //총 페이지 수를 계산
                " from Feeds f " + // 피드페이지로 부터
                "join  f.comments c" + // commnets를 들고 온다
                " where  c.userComments.userId = : meId") // 유저 아이디가 나
    Page<Feeds> findFeedsByCommentsBy(@Param("meId") Long meId, Pageable pageable);
}