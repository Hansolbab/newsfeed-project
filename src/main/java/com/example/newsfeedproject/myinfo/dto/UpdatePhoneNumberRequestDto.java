package com.example.newsfeedproject.myinfo.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePhoneNumberRequestDto {
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호 변경은 11자리여야 합니다.")
    private String phoneNumber;
}
