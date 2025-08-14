package com.example.newsfeedproject.requestfollow.entity;


import com.example.newsfeedproject.follow.entity.FollowStatus;
import com.example.newsfeedproject.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "requestFollow", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"requesterId", "targetId"})
})
@Getter
public class RequestFollows {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestFollowId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requesterId", nullable = false)
    private Users requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "targetId", nullable = false)
    private Users target;


    @Enumerated(EnumType.STRING)
    @Column(name = "followStatus", nullable = false)
    @Setter(AccessLevel.NONE)
    private FollowStatus followStatus;

    @Column( name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @PrePersist
    public void prePersist() { // 다시 팔로우 시 새로 세팅
        this.createdAt = LocalDateTime.now();
    }


    public RequestFollows(){}

    public RequestFollows(Users requester, Users target){
        this.requester =requester;
        this.target = target;
        this.followStatus = FollowStatus.NONE;
    }


    public void request() {
        this.followStatus = FollowStatus.REQUESTED;
    }

    public void accept() {
        this.followStatus = FollowStatus.ACCEPTED;
    }

    public void reject() {
        this.followStatus = FollowStatus.REJECTED;
    }

    public void cancel() {
        this.followStatus = FollowStatus.NONE;
    }
}
