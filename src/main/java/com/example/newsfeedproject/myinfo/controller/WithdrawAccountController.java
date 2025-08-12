package com.example.newsfeedproject.myinfo.controller;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.myinfo.dto.WithdrawAccountRequestDto;
import com.example.newsfeedproject.myinfo.service.WithdrawAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/myinfo/modify")
@RequiredArgsConstructor
public class WithdrawAccountController {
    private final WithdrawAccountService withdrawAccountService;

    @DeleteMapping("/delete")
  public ResponseEntity<Void>  withdraw(
            @AuthenticationPrincipal UserDetailsImpl me,
            @Valid @RequestBody WithdrawAccountRequestDto dto
            ){
        withdrawAccountService.withdraw(me.getUserId(), dto.getPassword());
        return ResponseEntity.noContent().build();
    }
}
