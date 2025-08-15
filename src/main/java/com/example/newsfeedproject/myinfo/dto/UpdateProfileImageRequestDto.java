package com.example.newsfeedproject.myinfo.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateProfileImageRequestDto {
    @Size(max = 255, message = "프로필 이미지 URL은 255 이하여야 합니다.")
    private String profileImageUrl;
    public UpdateProfileImageRequestDto(String  profileImageUrl) {
        this.profileImageUrl=profileImageUrl;
    }
}
