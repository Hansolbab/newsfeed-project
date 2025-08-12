package com.example.newsfeedproject.follow.Controller;


import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.common.dto.ReadFollowUsersDto;
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
    public ResponseEntity<Void> follow(@PathVariable @NotNull Long userId,
                                      @AuthenticationPrincipal  UserDetailsImpl userDetails

    ) {

      Long meId = userDetails.getUserId();


        followsService.follow(meId, userId);


        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{userId}/unfollow")
    public ResponseEntity<Void> unfollow(@PathVariable @NotNull Long userId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long meId = userDetails.getUserId();

        followsService.unfollow(meId, userId);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    //내 목록 조회
    @GetMapping("/followers")
    public ResponseEntity<Page<ReadFollowUsersDto>> readFollowerMeList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size =  10, // 페이지에 들어올 수
                    sort = "createdAt", // 생성 시간으로 정렬
                    direction = Sort.Direction.DESC //내림차순 정렬 일단 최신순 정렬인데, 오래된 순으로도 가능합니다.
            )Pageable pageable) {

       Long meId = userDetails.getUserId();

      Page<ReadFollowUsersDto> followerMeList = followsService.readFollowerList(meId, meId, pageable);



        return new ResponseEntity<>(followerMeList, HttpStatus.OK);

    }


    //상대 팔로워 목록 조회
    @GetMapping("/{userId}/followers")
    public ResponseEntity<Page<ReadFollowUsersDto>> readFollowerList(
            @PathVariable @NotNull Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size =  10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable)
    {

        Long meId = userDetails.getUserId();

        return new ResponseEntity<>(followsService.readFollowerList(meId, userId ,pageable), HttpStatus.OK);
    }


    //내가 팔로우 하는 사람들
    @GetMapping("/followees")
    public ResponseEntity<Page<ReadFollowUsersDto>> readFolloweeMeList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size =  10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable) {

        Long meId = userDetails.getUser().getUserId();

        Page<ReadFollowUsersDto> followeeMeList = followsService.readFolloweeList(meId, meId, pageable);


        return new ResponseEntity<>(followeeMeList, HttpStatus.OK);
    }
    //유저가 팔로우 하는 사람들
    @GetMapping("/{userId}/followees")
    public ResponseEntity<Page<ReadFollowUsersDto>> readFolloweeList(
            @PathVariable @NotNull Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size =  10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable) {

        Long meId = userDetails.getUserId();

        return new ResponseEntity<>(followsService.readFolloweeList(userId, meId, pageable), HttpStatus.OK);
    }

}

