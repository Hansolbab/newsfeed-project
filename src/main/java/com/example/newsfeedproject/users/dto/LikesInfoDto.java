package com.example.newsfeedproject.users.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class LikesInfoDto {
    private Integer likeTotal;
    private boolean liked;

    public LikesInfoDto(Integer likeTotal, boolean liked) {
        this.likeTotal = likeTotal;
        this.liked = liked;
    }
}
