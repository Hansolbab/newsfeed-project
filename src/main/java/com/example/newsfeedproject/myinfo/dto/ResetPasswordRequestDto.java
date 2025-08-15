package com.example.newsfeedproject.myinfo.dto;

import lombok.Getter;

@Getter
public class ResetPasswordRequestDto {
    private String oldPassword;
    private String newPassword;
    private String newConfirmPassword;

    public  ResetPasswordRequestDto(String oldPassword, String newPassword, String newConfirmPassword) {
        this.oldPassword=oldPassword;
        this.newPassword=newPassword;
        this.newConfirmPassword=newConfirmPassword;
    }
    public boolean isNewPasswordMatch(){
        return newPassword.equals(newConfirmPassword);
    }
}
