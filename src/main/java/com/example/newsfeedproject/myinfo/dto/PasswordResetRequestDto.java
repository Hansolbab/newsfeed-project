package com.example.newsfeedproject.myinfo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequestDto {
    @NotBlank(message = "기존 비밀번호를 입력해 주세요")
    private String oldPassword;

    @NotBlank(message = "새 비밀번호를 입력해 주세요")
    private String newPassword;

    @NotBlank(message = "비밀번호 확인란을 입력해 주세요")
    private String newConfirmPassword;

    public boolean isNewPasswordMatch(){
        return newPassword.equals(newConfirmPassword);
    }
}
