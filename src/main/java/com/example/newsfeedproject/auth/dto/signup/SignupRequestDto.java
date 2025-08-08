package com.example.newsfeedproject.auth.dto.signup;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    @NotBlank
    @Size(min = 2, max = 8, message = "최소 2글자 이상 8글자 이하로 작성해주세요.")
    private String userName;

    @NotBlank
    // 이메일은 정규식 사용
    @Email(message = "유효한 이메일 형식을 입력해주세요.")
    private String email;

    @NotBlank
    @Pattern(
            // 전화번호 - 있어도 되고 없어도 되게.
            regexp = "^\\d{2,3}-?\\d{3,4}-?\\d{4}$",
            message = "전화번호 형식이 올바르지 않습니다."
    )
    private String phoneNumber;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
            message = "8글자 이상 소문자 포함 영문 + 숫자 + 특수문자를  최소 1글자씩 포함합니다."
    )
    private String password;

    @NotBlank(message = "비밀번호가 일치하지 않습니다.")
    private String confirmPassword;

    @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
    public boolean isPasswordsMatch() {
        return password.equals(confirmPassword);
    }
}
