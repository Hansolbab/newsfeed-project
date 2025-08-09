package com.example.newsfeedproject.common.dto;


import com.example.newsfeedproject.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadFollowUsersDto {

    private Long userId;
    private String userName;
    private String profileImg;
    private boolean followByMe;

    public ReadFollowUsersDto(Users users, boolean followByMe) {
        this.userId = users.getUserId();
        this.userName= users.getUserName();
        this.profileImg = users.getProfileImg();
        this.followByMe= followByMe;
    }

//    public static ReadFollowUsersDto toDto(Users user, boolean followByMe) {
//
//        return new ReadFollowUsersDto(
//                user.getUserId(),
//                user.getUserName(),
//                user.getProfileImg(),
//                followByMe
//        );
//    }


}
