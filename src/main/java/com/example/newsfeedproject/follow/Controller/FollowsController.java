package com.example.newsfeedproject.follow.Controller;


import com.example.newsfeedproject.auth.impl.UserDetailsImpl;

import com.example.newsfeedproject.follow.dto.FollowResponseDto;
import com.example.newsfeedproject.follow.dto.ReadFollowUsersDto;
import com.example.newsfeedproject.follow.service.FollowsService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
@Validated
public class FollowsController {

    private final FollowsService followsService;


    @PostMapping("/{userId}/follow") // 팔로우 url 추가
    public ResponseEntity<FollowResponseDto> follow(
            @PathVariable @NotNull Long userId,
            @AuthenticationPrincipal  UserDetailsImpl userDetails
    ) {
        return new ResponseEntity<>(followsService.follow(userDetails, userId), HttpStatus.OK);
    }



    @PostMapping("/{userId}/delete")
    public  ResponseEntity<FollowResponseDto> deleteFollow(
            @PathVariable@NotNull Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return new ResponseEntity<>(followsService.deleteFollow(userDetails, userId), HttpStatus.OK);
    }

    //내 목록 조회
    @GetMapping("/followers")
    public ResponseEntity<Page<ReadFollowUsersDto>> readFollowerMeList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
    ) {

       Long meId = userDetails.getUserId();

        return new ResponseEntity<>(followsService.readFollowerList(meId, meId, pageable), HttpStatus.OK);

    }


    //상대 팔로워 목록 조회
    @GetMapping("/{userId}/followers")
    public ResponseEntity<Page<ReadFollowUsersDto>> readFollowerList(
            @PathVariable @NotNull Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable)
    {

        Long meId = userDetails.getUserId();

        return new ResponseEntity<>(followsService.readFollowerList(meId, userId ,pageable), HttpStatus.OK);
    }


    //내가 팔로우 하는 사람들
    @GetMapping("/followees")
    public ResponseEntity<Page<ReadFollowUsersDto>> readFolloweeMeList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
    ) {

        Long meId = userDetails.getUser().getUserId();

        return new ResponseEntity<>(followsService.readFolloweeList(meId, meId, pageable), HttpStatus.OK);
    }

    //유저가 팔로우 하는 사람들
    @GetMapping("/{userId}/followees")
    public ResponseEntity<Page<ReadFollowUsersDto>> readFolloweeList(
            @PathVariable @NotNull Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
    ) {

        Long meId = userDetails.getUserId();

        return new ResponseEntity<>(followsService.readFolloweeList(meId, userId, pageable), HttpStatus.OK);
    }

}

