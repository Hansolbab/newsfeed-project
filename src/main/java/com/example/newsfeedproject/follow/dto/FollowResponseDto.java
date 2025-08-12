package com.example.newsfeedproject.follow.dto;


import com.example.newsfeedproject.follow.entity.FollowStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowResponseDto {

    private final boolean followed;
    private final FollowStatus status;
}
