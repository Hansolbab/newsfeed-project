package com.example.newsfeedproject.users.dto;

import lombok.Getter;

@Getter
public class ReadUserSimpleResponseDto {
    private String userName;
    private String profileImg;

    public ReadUserSimpleResponseDto(String userName, String profileImg) {
        this.userName = userName;
        this.profileImg = profileImg;
    }
}
