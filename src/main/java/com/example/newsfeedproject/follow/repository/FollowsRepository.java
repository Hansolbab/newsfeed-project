package com.example.newsfeedproject.follow.repository;


import com.example.newsfeedproject.follow.entity.Follows;
import com.example.newsfeedproject.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FollowsRepository extends JpaRepository<Follows , Long> {

    Optional<Follows> findByFollowerAndFollowee(Users follower, Users followee);

    List<Follows> findByFollowee(Users followee);

    List<Follows> findByFollower(Users followerMe);
}
