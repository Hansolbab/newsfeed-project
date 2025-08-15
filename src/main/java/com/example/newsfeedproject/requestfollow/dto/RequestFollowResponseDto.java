package com.example.newsfeedproject.requestfollow.dto;


import com.example.newsfeedproject.follow.entity.FollowStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequestFollowResponseDto {
    private final FollowStatus followStatus;
}
