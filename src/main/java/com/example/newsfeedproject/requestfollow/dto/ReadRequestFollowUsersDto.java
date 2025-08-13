package com.example.newsfeedproject.requestfollow.dto;


import com.example.newsfeedproject.requestfollow.entity.RequestFollows;
import com.example.newsfeedproject.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadRequestFollowUsersDto {

    private Long userId;
    private String userName;
    private String profileImageUrl;


    public ReadRequestFollowUsersDto(Users users) {
        this.userId = users.getUserId();
        this.userName= users.getUserName();
        this.profileImageUrl = users.getProfileImageUrl();
    }

    public static ReadRequestFollowUsersDto todto(RequestFollows follower) {

        return new ReadRequestFollowUsersDto(
                follower.getRequester().getUserId(),
                follower.getRequester().getUserName(),
                follower.getRequester().getProfileImageUrl()
        );

    }
}
