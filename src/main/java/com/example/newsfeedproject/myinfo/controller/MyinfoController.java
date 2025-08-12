package com.example.newsfeedproject.myinfo.controller;


import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.common.dto.PrincipalRequestDto;
import com.example.newsfeedproject.common.dto.ReadUserSimpleResponseDto;
import com.example.newsfeedproject.feeds.dto.FeedResponseDto;
import com.example.newsfeedproject.myinfo.service.MyinfoService;
import com.example.newsfeedproject.users.dto.ReadUsersFeedsResponseDto;
import com.example.newsfeedproject.users.service.UsersService;
import lombok.RequiredArgsConstructor;

import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/myinfo")
@RequiredArgsConstructor
public class MyinfoController {
    private final UsersService usersService;
    private final MyinfoService myinfoService;


    @GetMapping("/{userId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ReadUserSimpleResponseDto> readMySimple(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        if(userDetails==null){
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        PrincipalRequestDto principalUser = new PrincipalRequestDto(userDetails.getUserId(),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));

        ReadUserSimpleResponseDto readMySimpleProfile = usersService.readUserSimple(userId, principalUser);
        return ResponseEntity.ok(readMySimpleProfile);
    }

    @GetMapping("/{userId}/feeds")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ReadUsersFeedsResponseDto>> readMyFeeds(
            @PathVariable Long userId,                              // 본인 확인용 당사자 userId값
            @AuthenticationPrincipal UserDetailsImpl userDetails,   // 본인 확인용 로그인한 userId값
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        // 로그인 안한 경우
        if (userDetails==null) {throw new IllegalIdentifierException("로그인 필요");}

        PrincipalRequestDto principalUser = new PrincipalRequestDto(
                userDetails.getUserId(),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));

        Page<ReadUsersFeedsResponseDto> readMyFeedPage = usersService.readUserFeed(userId, principalUser, pageable);

        return new  ResponseEntity<>(readMyFeedPage, HttpStatus.OK);
    }



    @GetMapping("/commentfeeds")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<FeedResponseDto>> readFeedsByMyComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
    ) {
        if(userDetails == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

       return new ResponseEntity<>(myinfoService.readFeedsByMyCommnet(userDetails, pageable), HttpStatus.OK);

    }

}
