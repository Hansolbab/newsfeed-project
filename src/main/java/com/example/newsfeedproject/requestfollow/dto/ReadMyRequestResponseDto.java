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


    public static ReadMyRequestResponseDto toDto(RequestFollows requestFollows) {
        Users target = requestFollows.getTarget();

        return new ReadMyRequestResponseDto(
                target.getUserId(),
                target.getUserName(),
                target.getProfileImageUrl()
        );

    }
}
