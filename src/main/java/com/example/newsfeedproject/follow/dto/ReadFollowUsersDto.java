package com.example.newsfeedproject.follow.dto;

import com.example.newsfeedproject.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadFollowUsersDto {
    private Long userId;
    private String userName;
    private String profileImageUrl;
    private boolean followByMe;

    public ReadFollowUsersDto(Users users, boolean followByMe) {
        this.userId = users.getUserId();
        this.userName= users.getUserName();
        this.profileImageUrl = users.getProfileImageUrl();
        this.followByMe= followByMe;// 나랑 팔로우 되어 있는가?
    }

    public static ReadFollowUsersDto todto(Users follower, boolean contains) {
      return new ReadFollowUsersDto(follower, contains);
    }
}
