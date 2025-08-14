package com.example.newsfeedproject.users.controller;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.common.dto.ReadUserSimpleResponseDto;
import com.example.newsfeedproject.common.dto.ReadUsersFeedsResponseDto;
import com.example.newsfeedproject.users.dto.SearchUserResponseDto;
import com.example.newsfeedproject.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;

    @GetMapping("/{userId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ReadUserSimpleResponseDto> readUserSimple(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        // ReadUserSimpleResponseDto 형태로 반환
        ReadUserSimpleResponseDto readUserSimpleProfile = usersService.readUserSimple(userId, userDetails);

        return new ResponseEntity<>(readUserSimpleProfile, HttpStatus.OK);
    }

    @GetMapping("/{userId}/feeds")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ReadUsersFeedsResponseDto>> readUserFeeds(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
    ){

        Page<ReadUsersFeedsResponseDto> userFeedPage = usersService.readUserFeed(userId, userDetails, pageable);

        return new ResponseEntity<>(userFeedPage, HttpStatus.OK);
    }

    @GetMapping("/search")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<SearchUserResponseDto>> searchUsers(
            @RequestParam String keyword,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault() Pageable pageable){

        Page<SearchUserResponseDto> searchUserPage = usersService.searchUser(keyword, userDetails, pageable);

        return new ResponseEntity<>(searchUserPage, HttpStatus.OK);
    }
}
