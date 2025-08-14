package com.example.newsfeedproject.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadUserSimpleResponseDto {
    private String userName;
    private String profileImg;
    private boolean followed;

    public ReadUserSimpleResponseDto(String userName, String profileImg, boolean followed) {
        this.userName = userName;
        this.profileImg = profileImg;
        this.followed = followed;
    }
}
