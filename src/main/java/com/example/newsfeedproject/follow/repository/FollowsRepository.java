package com.example.newsfeedproject.follow.repository;


import com.example.newsfeedproject.follow.entity.Follows;
import com.example.newsfeedproject.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface FollowsRepository extends JpaRepository<Follows , Long> {

    Optional<Follows> findByFollowerAndFollowee(Users follower, Users followee);

    Page<Follows> findByFollowee(Users followee, Pageable pageable);

    Page<Follows> findByFollower(Users followerMe, Pageable pageable);


    @Query("select f.followee.userId " + //팔로우 당하는 사람의 아이디 값을 찾아온다.
            "from  Follows f" + // 팔로우 테이블로부터
            " where f.follower.userId = :meId")
        // 내가 팔로워하는 사람과
    Set<Long> findFolloweeIdsOf(@Param("meId") Long meId);

    // meId가 userId를 팔로우 할 때 True, 단건
    boolean existsByFollower_UserIdAndFollowee_UserIdAndFollowedTrue(Long meId, Long userId);

    @Query("SELECT f.followee.userId, f.followed " +
            "FROM Follows f " +
            "WHERE f.follower.userId = :meId AND f.followee.userId IN :userId ")
    List<Object[]> isFollowedByMyIdANDUserIds(@Param("meId") Long meId, @Param("userId") List<Long> userId);
}
