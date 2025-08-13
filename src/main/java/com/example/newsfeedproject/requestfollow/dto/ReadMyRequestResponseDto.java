package com.example.newsfeedproject.requestfollow.dto;

import com.example.newsfeedproject.requestfollow.entity.RequestFollows;
import com.example.newsfeedproject.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ReadMyRequestResponseDto {

    private Long userId;
    private String userName;
    private String profileImageUrl;


    public ReadMyRequestResponseDto(Users users) {
        this.userId = users.getUserId();
        this.userName= users.getUserName();
        this.profileImageUrl = users.getProfileImageUrl();
    }

    public static ReadMyRequestResponseDto todto(RequestFollows target) {

        return new ReadMyRequestResponseDto(
                target.getRequester().getUserId(),
                target.getRequester().getUserName(),
                target.getRequester().getProfileImageUrl()
        );

    }
}
