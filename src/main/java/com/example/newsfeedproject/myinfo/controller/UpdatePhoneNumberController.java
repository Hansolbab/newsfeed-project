package com.example.newsfeedproject.myinfo.controller;


import com.example.newsfeedproject.myinfo.dto.UpdatePhoneNumberDto;
import com.example.newsfeedproject.myinfo.service.UpdatePhoneNumberService;
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
public class UpdatePhoneNumberController {
    private final UpdatePhoneNumberService updatePhoneNumberService;

    @PostMapping("/phonenumber")
    public ResponseEntity<String> updatePhoneNumber(
            @AuthenticationPrincipal com.example.newsfeedproject.auth.impl.UserDetailsImpl me,
            @Valid @RequestBody UpdatePhoneNumberDto dto) {

        updatePhoneNumberService.update(me.getUserId(), dto.getPhoneNumber());
        return ResponseEntity.ok("전화번호 변경이 완료되었습니다.");
    }
}
