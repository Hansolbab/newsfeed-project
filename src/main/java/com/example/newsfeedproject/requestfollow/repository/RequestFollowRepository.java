package com.example.newsfeedproject.requestfollow.repository;


import com.example.newsfeedproject.follow.entity.FollowStatus;

import com.example.newsfeedproject.requestfollow.entity.RequestFollows;
import com.example.newsfeedproject.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestFollowRepository extends JpaRepository<RequestFollows, Long> {

    Optional<RequestFollows> findByRequesterAndTarget(Users requester, Users target);



    Page<RequestFollows> findByTargetAndFollowStatus(
            Users target,
            FollowStatus followStatus,
            Pageable pageable);

    Page<RequestFollows> findByRequesterAndFollowStatus(Users requester, FollowStatus followStatus, Pageable pageable);

//    boolean existByRequester_RequesterIdAndTarget_TargetId(Long requesterId,Long targeterId );

//    Optional<Boolean> existByRequesterAndTargetAndFollowStatus(Users requester, Users target, FollowStatus followStatus);

}
