package com.example.newsfeedproject.myinfo.controller;

import com.example.newsfeedproject.myinfo.dto.PasswordResetRequestDto;
import com.example.newsfeedproject.myinfo.service.PasswordResetService;
import com.example.newsfeedproject.users.entity.Users;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/myinfo")
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @PostMapping("password")
    public ResponseEntity<String> resetPassword(Authentication authentication, @Valid @RequestBody PasswordResetRequestDto dto) {

        //비밀번호가 일치하는지 확인
        if(!dto.isNewPasswordMatch()){
            return ResponseEntity.badRequest().body("새 비밀번호와 비밀번호 확인이 일치하지 않습니다. ");
        }

        //비밀번호 재설정 처리
        passwordResetService.resetPassword(authentication, dto.getOldPassword(),  dto.getNewPassword());

        return ResponseEntity.ok("비밀전호가 성공적으로 변경되었습니다.");
    }
}
