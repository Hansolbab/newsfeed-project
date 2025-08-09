package com.example.newsfeedproject.follow.entity;


import com.example.newsfeedproject.users.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "follows" ,uniqueConstraints = {
        @UniqueConstraint(columnNames = {"followerId" ,"followeeId"}) // @사용자가 같은 유저를 팔로우 하지 못하게 하기 위함
})
@Setter
@Getter
public class Follows {



    //Follow Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "followId", nullable = false)
    private Long followId;


    //팔로우 하는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followerId", nullable = false)
    private Users follower;


    //팔로우 당하는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followeeId", nullable = false)
    private Users followee;

    //팔로우 이력 기록
    @Column(name = "isFollowed", nullable = false)
    //isFollowed Getter 생성 금지
    private boolean followed;




}
