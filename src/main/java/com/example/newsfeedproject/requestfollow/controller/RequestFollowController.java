package com.example.newsfeedproject.requestfollow.controller;


import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.requestfollow.dto.ReadMyRequestResponseDto;
import com.example.newsfeedproject.requestfollow.dto.ReadRequestFollowUsersDto;
import com.example.newsfeedproject.requestfollow.dto.RequestFollowResponseDto;
import com.example.newsfeedproject.requestfollow.service.RequestFollowService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/follows/private")
public class RequestFollowController {

    private final RequestFollowService requestFollowService;


    @PostMapping("/{userId}/request")
    public ResponseEntity<RequestFollowResponseDto> requestFollow(
            @PathVariable @NotNull Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        return new ResponseEntity<>(requestFollowService.requestFollow(userDetails, userId), HttpStatus.OK);
    }

    @PostMapping("/{userId}/accept")
    public ResponseEntity<RequestFollowResponseDto> acceptFollow(
            @PathVariable @NotNull Long userId,
            @AuthenticationPrincipal  UserDetailsImpl userDetails
    ) {


        return new ResponseEntity<>(requestFollowService.acceptFollow(userDetails, userId), HttpStatus.OK);
    }

    @PostMapping("/{userId}/reject")
    public ResponseEntity<RequestFollowResponseDto> rejectFollow(
            @PathVariable @NotNull Long userId,
            @AuthenticationPrincipal  UserDetailsImpl userDetails
    ) {
        return new ResponseEntity<>(requestFollowService.rejectedFollow(userDetails, userId), HttpStatus.OK);
    }



    @PostMapping("/{userId}/cancel")
    public ResponseEntity<RequestFollowResponseDto> cancelFollow(
            @PathVariable @NotNull Long userId,
            @AuthenticationPrincipal  UserDetailsImpl userDetails
    ) {
        return new ResponseEntity<>(requestFollowService.cancelFollow(userDetails, userId), HttpStatus.OK);
    }


    //나에게 요청을 보내는 사람
    @GetMapping("/requestMe")
    public ResponseEntity<Page<ReadRequestFollowUsersDto>> readRequestMe(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new ResponseEntity<>(requestFollowService.readRequestMe(userDetails, pageable), HttpStatus.OK);
    }

    //내가 요청한 사람
    @GetMapping("/myRequest")
    public ResponseEntity<Page<ReadMyRequestResponseDto>> readMyRequest(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new ResponseEntity<>(requestFollowService.readMyRequest(userDetails, pageable), HttpStatus.OK);
    }

}
