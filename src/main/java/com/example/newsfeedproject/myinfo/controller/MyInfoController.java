package com.example.newsfeedproject.myinfo.controller;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.common.dto.ReadUserSimpleResponseDto;
import com.example.newsfeedproject.common.exception.users.UsersErrorException;
import com.example.newsfeedproject.feeds.dto.ReadFeedsResponseDto;
import com.example.newsfeedproject.myinfo.service.MyInfoService;
import com.example.newsfeedproject.common.dto.ReadUsersFeedsResponseDto;
import com.example.newsfeedproject.users.entity.AccessAble;
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
import static com.example.newsfeedproject.common.exception.users.UsersErrorCode.*;

@RestController
@RequestMapping("/api/myInfo")
@RequiredArgsConstructor
public class MyInfoController {
    private final UsersService usersService;
    private final MyInfoService myInfoService;

    @GetMapping("/{userId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ReadUserSimpleResponseDto> readMySimple(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ReadUserSimpleResponseDto readMySimpleProfile = usersService.readUserSimple(userId, userDetails);

        return ResponseEntity.ok(readMySimpleProfile);
    }

    @GetMapping("/{userId}/feeds")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ReadUsersFeedsResponseDto>> readMyFeeds(
            @PathVariable Long userId,                              // 본인 확인용 당사자 userId값
            @AuthenticationPrincipal UserDetailsImpl userDetails,   // 본인 확인용 로그인한 userId값
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        Page<ReadUsersFeedsResponseDto> readMyFeedPage = usersService.readUserFeed(userId, userDetails, pageable);

        return new  ResponseEntity<>(readMyFeedPage, HttpStatus.OK);
    }

    @GetMapping("/commentFeeds")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ReadFeedsResponseDto>> readFeedsByMyComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
    ) {
        if(userDetails == null) {
            throw new UsersErrorException(LOGIN_REQUIRED);
        }

       return new ResponseEntity<>(myInfoService.readFeedsByMyComment(userDetails, pageable), HttpStatus.OK);

    }


    @GetMapping("/likeFeeds")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ReadFeedsResponseDto>> readFeedsByMyLikes(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault( sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {


        return new ResponseEntity<>(myInfoService.readFeedsByMyLikes(userDetails, pageable) ,HttpStatus.OK);
    }

    @PostMapping("/accessAble")
    @Transactional
    public ResponseEntity<String> accessAbleMyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails, AccessAble accessAble){
        return new ResponseEntity<>(myInfoService.accessAlbeMyPage(userDetails, accessAble),HttpStatus.OK);
    }
}
