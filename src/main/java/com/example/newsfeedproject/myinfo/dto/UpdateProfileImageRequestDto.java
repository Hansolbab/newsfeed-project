package com.example.newsfeedproject.myinfo.dto;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileImageRequestDto {
    @NotBlank(message = "프로필 이미지 URL은 필수입니다.")
    @Size(max = 255, message = "프로필 이미지 URL은 255 이하여야 합니다.")
    private String profileImageUrl;
}
