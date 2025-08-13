package com.example.newsfeedproject.myinfo.controller;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.common.dto.PrincipalRequestDto;
import com.example.newsfeedproject.users.dto.ReadUserSimpleResponseDto;
import com.example.newsfeedproject.feeds.dto.FeedsResponseDto;
import com.example.newsfeedproject.myinfo.dto.UpdateProfileImageRequestDto;
import com.example.newsfeedproject.myinfo.service.MyInfoService;
import com.example.newsfeedproject.myinfo.service.ProfileImageService;
import com.example.newsfeedproject.common.dto.ReadUsersFeedsResponseDto;
import com.example.newsfeedproject.users.service.UsersService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/myInfo")
@RequiredArgsConstructor
public class MyInfoController {
    private final UsersService usersService;
    private final MyInfoService myInfoService;
    private final ProfileImageService profileImageService ;

    @GetMapping("/{userId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ReadUserSimpleResponseDto> readMySimple(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
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
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
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

    @GetMapping("/commentFeeds")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<FeedsResponseDto>> readFeedsByMyComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
    ) {
        if(userDetails == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

       return new ResponseEntity<>(myInfoService.readFeedsByMyComment(userDetails, pageable), HttpStatus.OK);

    }

    // 내 프로필 이미지 URL로 수정
    @PutMapping("/profileImage")
    public ResponseEntity<String> uploadProfileImage(
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            @Valid @RequestBody UpdateProfileImageRequestDto updateProfileImageRequestDto
    ){
      profileImageService.updateProfileImageUrl(userDetailsImpl.getUserId(), updateProfileImageRequestDto.getProfileImageUrl());
      return ResponseEntity.ok("프로필 이미지 변경이 완료되었습니다.");
    }
    // 내 프로필 이미지 삭제
    @DeleteMapping("/profileImage")
    public ResponseEntity<Void> deleteProfileImage(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl){
        profileImageService.setPlaceholderUrl(userDetailsImpl.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/likeFeeds")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<FeedsResponseDto>> readFeedsByMyLikes(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault( sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if(userDetails == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        return new ResponseEntity<>(myInfoService.readFeedsByMyLikes(userDetails, pageable) ,HttpStatus.OK);
    }

}
