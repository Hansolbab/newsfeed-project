package com.example.newsfeedproject.feeds.repository;

import com.example.newsfeedproject.category.entity.Category;
import com.example.newsfeedproject.feeds.entity.Feeds; // Feeds 엔티티 임포트
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
// Feeds 엔티티 데이터 접근
@Repository
public interface FeedsRepository extends JpaRepository<Feeds, Long> {
    // 삭제되지 않은 게시글 전체 조회
    Page<Feeds> findByDeletedFalse(Pageable pageable);

    // 삭제되지 않은 게시글 단건 조회
    Optional<Feeds> findByFeedIdAndDeletedFalse(Long feedId);

    Page<Feeds> findByCategory(Category category, Pageable pageable);
    // 필요한 사용자 정의 쿼리 메소드 추가 가능
    Page<Feeds> findByUser_UserId(Long userId, Pageable pageable);

    // f.deleted = false 추가했음 확인 요망
    @Query(value = "select f from Feeds f" + // 실제 데이터 조회용
            " join f.comments c" + // Feeds에 매핑된 comments 컬렉션을 들고 온다.
            " where  f.deleted = false And c.userComments.userId = :meId" + // 유저 아이디가 나
            " group by  f.feedId " + // 내가 단 여러개의 댓글을 그룹화
            "order by max(c.createdAt) desc", // 내 댓글 기준으로 최신순
        countQuery = "select count(distinct f.feedId)" + //총 페이지 수를 계산
                " from Feeds f " + // 피드페이지로 부터
                "join  f.comments c" + // commnets를 들고 온다
                " where  c.userComments.userId = : meId") // 유저 아이디가 나
    Page<Feeds> findFeedsByCommentsBy(@Param("meId") Long meId, Pageable pageable);

    @Query(value = "select f from Feeds f where f.feedId in :feedIds " ,
              countQuery = "select count(f) from Feeds f where f.feedId in :feedIds")
    Page<Feeds> findByIdIn(@Param("feedIds")Set<Long> feedIds, Pageable pageable);

    // FeedsService.readAllFeeds 에서 사용될 쿼리 // 테스트.. 언제해봐..
    @Query("SELECT f FROM Feeds f " +
            "WHERE f.deleted = false AND (" + // 소프트 삭제되지 않은 게시글
            // 1. 자신의 게시글은 언제나 접근 가능
            "   f.user.userId = :currentUserId OR " +
            // 2. 다른 사람의 게시글인 경우, 프로필 접근 레벨 및 게시글 접근 레벨에 따라 판단
            "   (f.user.userId <> :currentUserId AND (" + // 게시글 주인이 내가 아닌 경우 ⭐
            // A. 프로필이 '전체 공개' (ALL_ACCESS)인 경우:
            "       (f.user.visibility = 'ALL_ACCESS' AND (" +
            "           f.accessAble = 'ALL_ACCESS' OR " + // 게시글도 전체 공개 이거나
            "           (f.accessAble = 'FOLLOWER_ACCESS' AND EXISTS (SELECT fw FROM Follows fw WHERE fw.follower.userId = :currentUserId AND fw.followee.userId = f.user.userId AND fw.followed = true))" + // 게시글은 팔로워 공개인데, 내가 팔로우하는 경우
            "       )) OR " +
            // B. 프로필이 '팔로워 공개' (FOLLOWER_ACCESS)인데, 내가 작성자를 팔로우하는 경우:
            "       (f.user.visibility = 'FOLLOWER_ACCESS' AND EXISTS (SELECT fw FROM Follows fw WHERE fw.follower.userId = :currentUserId AND fw.followee.userId = f.user.userId AND fw.followed = true) AND (" +
            "           f.accessAble = 'ALL_ACCESS' OR " + // 게시글은 전체 공개 이거나
            "           (f.accessAble = 'FOLLOWER_ACCESS' AND EXISTS (SELECT fw FROM Follows fw WHERE fw.follower.userId = :currentUserId AND fw.followee.userId = f.user.userId AND fw.followed = true))" + // 게시글도 팔로워 공개 & 내가 팔로우 (중복 확인)
            "       )) " +
            // (C. 프로필이 '나만 보기' (NONE_ACCESS)인 경우는 여기에 포함되지 않음. 본인 외에는 볼 수 없으므로 쿼리에서 자동 제외됨)
            "   ))" +
            ")" +
            "ORDER BY " +
            "case " +
            "when EXISTS (SELECT fw FROM Follows fw WHERE fw.follower.userId = :currentUserId AND fw.followee.userId = f.user.userId AND fw.followed = true) then 1  " +
            "when f.category = :likeCategory then 2 " +
            "when f.user.visibility = 'ALL_ACCESS' OR f.accessAble = 'ALL_ACCESS' then 3 " +
            "else 4 " +
            "end, f.createdAt desc")
    Page<Feeds> findAllFeedConditional(@Param("currentUserId") Long currentUserId, @Param("likeCategory") Category category, Pageable pageable);


    // FeedsService.readAllFeeds 에서 사용될 쿼리
    @Query("SELECT f FROM Feeds f " +
            "WHERE f.deleted = false AND (" + // 소프트 삭제되지 않은 게시글
            // 1. 자신의 게시글은 언제나 접근 가능
            "   f.user.userId = :currentUserId OR " +
            // 2. 다른 사람의 게시글인 경우, 프로필 접근 레벨 및 게시글 접근 레벨에 따라 판단
            "   (f.user.userId <> :currentUserId AND (" + // 게시글 주인이 내가 아닌 경우 ⭐
            // A. 프로필이 '전체 공개' (ALL_ACCESS)인 경우:
            "       (f.user.visibility = 'ALL_ACCESS' AND (" +
            "           f.accessAble = 'ALL_ACCESS' OR " + // 게시글도 전체 공개 이거나
            "           (f.accessAble = 'FOLLOWER_ACCESS' AND EXISTS (SELECT fw FROM Follows fw WHERE fw.follower.userId = :currentUserId AND fw.followee.userId = f.user.userId AND fw.followed = true))" + // 게시글은 팔로워 공개인데, 내가 팔로우하는 경우
            "       )) OR " +
            // B. 프로필이 '팔로워 공개' (FOLLOWER_ACCESS)인데, 내가 작성자를 팔로우하는 경우:
            "       (f.user.visibility = 'FOLLOWER_ACCESS' AND EXISTS (SELECT fw FROM Follows fw WHERE fw.follower.userId = :currentUserId AND fw.followee.userId = f.user.userId AND fw.followed = true) AND (" +
            "           f.accessAble = 'ALL_ACCESS' OR " + // 게시글은 전체 공개 이거나
            "           (f.accessAble = 'FOLLOWER_ACCESS' AND EXISTS (SELECT fw FROM Follows fw WHERE fw.follower.userId = :currentUserId AND fw.followee.userId = f.user.userId AND fw.followed = true))" + // 게시글도 팔로워 공개 & 내가 팔로우 (중복 확인)
            "       )) " +
            // (C. 프로필이 '나만 보기' (NONE_ACCESS)인 경우는 여기에 포함되지 않음. 본인 외에는 볼 수 없으므로 쿼리에서 자동 제외됨)
            "   ))" +
            ")")
    Page<Feeds> findAccessibleFeedsBasedOnProfile(@Param("currentUserId") Long currentUserId, Pageable pageable);

    // 1. 모든 소프트 삭제된 게시글 조회 (페이징 포함)
    Page<Feeds> findByDeletedTrue(Pageable pageable);

    // 2. 특정 소프트 삭제된 게시글 단건 조회
    Optional<Feeds> findByFeedIdAndDeletedTrue(Long feedId);

    // 본인이 피드 (소프트 딜리트 한거 제외) 찾기
    @Query("SELECT f FROM Feeds f " +
            "WHERE f.deleted = false AND f.user.userId = :userId ")
    Page<Feeds> findAcceessibleFeedsMyPage(@Param("userId") Long userId, Pageable pageable);

    // 다른 사람 피드 (소프트 딜리트 한거 제외) and 게시글 all_access 찾기
    @Query("SELECT f FROM Feeds f " +
            "WHERE f.deleted = false AND f.user.userId <> :userId AND f.accessAble = 'ALL_ACCESS' ")
    Page<Feeds> findAllAccessFeedsUserPage(@Param("userId") Long userId, Pageable pageable);

    // 다른 사람 피드 팔로워 공개 유저 (소프트 딜리트 한거 제외) and 게시글 all_access 찾기
    @Query("SELECT f FROM Feeds f " +
            "WHERE f.deleted = false AND f.user.userId <> :userId AND f.accessAble <> 'NONE_ACCESS' ")
    Page<Feeds> findFollowerAccessFeedsUserPage(@Param("userId") Long userId, Pageable pageable);


}