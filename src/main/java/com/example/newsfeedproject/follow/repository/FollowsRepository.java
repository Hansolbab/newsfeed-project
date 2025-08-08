package com.example.newsfeedproject.follow.repository;


import com.example.newsfeedproject.follow.entity.Follows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowsRepository extends JpaRepository<Follows , Long> {

    Optional<Follows> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
}
