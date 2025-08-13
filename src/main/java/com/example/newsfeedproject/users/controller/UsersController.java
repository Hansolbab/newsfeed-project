package com.example.newsfeedproject.users.controller;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.common.dto.PrincipalRequestDto;
import com.example.newsfeedproject.users.dto.ReadUserSimpleResponseDto;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

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
        // 로그인 안한 경우
        if (userDetails==null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        PrincipalRequestDto principalUser = new PrincipalRequestDto(userDetails.getUserId(),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));   // Set으로 변환해서 반환

        // ReadUserSimpleResponseDto 형태로 반환
        ReadUserSimpleResponseDto readUserSimpleProfile = usersService.readUserSimple(userId, principalUser);

        return new ResponseEntity<>(readUserSimpleProfile, HttpStatus.OK);
    }

    @GetMapping("/{userId}/feeds")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ReadUsersFeedsResponseDto>> readUserFeeds(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
    ){
        // 로그인 안한 경우
        if (userDetails==null) {throw new IllegalStateException("로그인 필요합니다.");}

        PrincipalRequestDto principalUser = new PrincipalRequestDto(userDetails.getUserId(),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));

        Page<ReadUsersFeedsResponseDto> userFeedPage = usersService.readUserFeed(userId, principalUser, pageable);

        return new ResponseEntity<>(userFeedPage, HttpStatus.OK);
    }

    @GetMapping("/search")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<SearchUserResponseDto>> searchUsers(
            @RequestParam String keyword,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault() Pageable pageable){

        //로그인 안한 경우
        if (userDetails==null) {throw new IllegalStateException("로그인 필요");}

        PrincipalRequestDto principalUser = new PrincipalRequestDto(userDetails.getUserId(),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));

        Page<SearchUserResponseDto> searchUserPage = usersService.searchUser(keyword, principalUser, pageable);

        return new ResponseEntity<>(searchUserPage, HttpStatus.OK);
    }
}
