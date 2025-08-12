package com.example.newsfeedproject.users.dto;

import lombok.Getter;

@Getter
public class SearchUserResponseDto {
    private String userName;
    private String profileImageUrl;
    private boolean followed;

    public SearchUserResponseDto(String userName, String profileImageUrl, boolean followed) {
        this.userName = userName;
        this.profileImageUrl = profileImageUrl;
        this.followed = followed;
    }

}
