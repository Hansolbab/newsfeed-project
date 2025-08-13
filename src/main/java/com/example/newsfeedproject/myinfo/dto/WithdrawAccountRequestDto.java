package com.example.newsfeedproject.myinfo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class WithdrawAccountRequestDto {
    @NotBlank(message = "현재 비밀번호를 입력해 주세요.")
    private String password;

}
