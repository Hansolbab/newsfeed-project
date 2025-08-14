package com.example.newsfeedproject.myinfo.controller;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.myinfo.dto.ResetPasswordRequestDto;
import com.example.newsfeedproject.myinfo.dto.UpdatePhoneNumberRequestDto;
import com.example.newsfeedproject.myinfo.dto.WithdrawAccountRequestDto;
import com.example.newsfeedproject.myinfo.service.ResetPasswordService;
import com.example.newsfeedproject.myinfo.service.UpdatePhoneNumberService;
import com.example.newsfeedproject.myinfo.service.WithdrawAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/myInfo/modify")
@RequiredArgsConstructor
public class MyInfoModifyController {
    private final UpdatePhoneNumberService updatePhoneNumberService;
    private final WithdrawAccountService withdrawAccountService;
    private final ResetPasswordService resetPasswordService;

    @PostMapping("/phoneNumber")
    public ResponseEntity<String> updatePhoneNumber(
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            @Valid @RequestBody UpdatePhoneNumberRequestDto updatePhoneNumberRequestDto
    ){

        updatePhoneNumberService.update(userDetailsImpl.getUserId(), updatePhoneNumberRequestDto.getPhoneNumber());
        return ResponseEntity.ok("전화번호 변경이 완료되었습니다.");
    }

    @PostMapping("/password")
    public ResponseEntity<String> resetPassword(
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            @Valid @RequestBody ResetPasswordRequestDto resetPasswordRequestDto
    ){
//        System.out.println("[CTRL] me=" + (me==null ? "null" : me.getUsername()));
        if (!resetPasswordRequestDto.isNewPasswordMatch()) {
            return ResponseEntity.badRequest().body("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
        resetPasswordService.resetPassword(userDetailsImpl, resetPasswordRequestDto.getOldPassword(), resetPasswordRequestDto.getNewPassword());
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void>  withdraw(
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            @Valid @RequestBody WithdrawAccountRequestDto withdrawAccountRequestDto
    ){
        withdrawAccountService.withdraw(userDetailsImpl.getUserId(), withdrawAccountRequestDto.getPassword());
        return ResponseEntity.noContent().build();
    }
}
