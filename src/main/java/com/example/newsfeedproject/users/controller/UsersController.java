package com.example.newsfeedproject.users.controller;

import com.example.newsfeedproject.common.dto.ReadUserSimpleResponseDto;
import com.example.newsfeedproject.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;

    @GetMapping("/{userId}")
    public ResponseEntity<ReadUserSimpleResponseDto> readUserSimple(@PathVariable Long userId){
        // ReadUserSimpleResponseDto 형태로 반환
        ReadUserSimpleResponseDto readUserSimpleProfile = usersService.readUserSimple(userId);

        return new ResponseEntity<>(readUserSimpleProfile, HttpStatus.OK);
    }

}
