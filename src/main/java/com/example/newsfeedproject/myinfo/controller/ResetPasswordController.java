package com.example.newsfeedproject.myinfo.controller;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.myinfo.dto.ResetPasswordRequestDto;
import com.example.newsfeedproject.myinfo.service.ResetPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/myinfo/modify")
@RequiredArgsConstructor
public class ResetPasswordController {
    private final ResetPasswordService passwordResetService;

    @PostMapping("/password")
    public ResponseEntity<String> resetPassword(
            @AuthenticationPrincipal UserDetailsImpl me,
            @Valid @RequestBody ResetPasswordRequestDto dto) {
        System.out.println("[CTRL] me=" + (me==null ? "null" : me.getEmail()));
        if (!dto.isNewPasswordMatch()) {
            return ResponseEntity.badRequest().body("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        passwordResetService.resetPassword(me, dto.getOldPassword(), dto.getNewPassword());
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }
}