package com.example.newsfeedproject.follow.Controller;


import com.example.newsfeedproject.common.dto.ReadFollowUsersDto;
import com.example.newsfeedproject.follow.service.FollowsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowsController {

    private final FollowsService followsService;


    @PostMapping("/{userId}/follow") // 팔로우 url 추가
    public ResponseEntity<Void> follow(@PathVariable Long userId,
                                       @RequestHeader(value = "X-User-Id", required = false) Long meId
    ) {

        followsService.follow(meId, userId);


        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{userId}/unfollow")
    public ResponseEntity<Void> unfollow(@PathVariable Long userId,
                                         @RequestHeader(value = "X-User-Id", required = false) Long meId) {


        followsService.unfollow(meId, userId);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    //내 목록 조회
    @GetMapping("/followers")
    public ResponseEntity<List<ReadFollowUsersDto>> readFollowerMeList(@RequestHeader(value = "X-User-Id", required = false) Long meId) {

        List<ReadFollowUsersDto> followerMeList = followsService.readFollowerList(meId, meId);

        if (followerMeList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(followerMeList, HttpStatus.OK);

    }


    //상대 팔로워 목록 조회
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<ReadFollowUsersDto>> readFollowerList(@PathVariable Long userId,
                                                                     @RequestHeader(value = "X-User-Id", required = false) Long meId) {

        List<ReadFollowUsersDto> followerList = followsService.readFollowerList(meId, userId);

        if (followerList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return new ResponseEntity<>(followerList, HttpStatus.OK);
    }


    //내가 팔로우 하는 사람들
    @GetMapping("/followes")
    public ResponseEntity<List<ReadFollowUsersDto>> readFolloweeMeList(@RequestHeader(value = "X-User-Id", required = false) Long meId) {

        List<ReadFollowUsersDto> followeeMeList = followsService.readFolloweeList(meId, meId);

        if (followeeMeList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return new ResponseEntity<>(followeeMeList, HttpStatus.OK);
    }
    //유저가 팔로우 하는 사람들
    @GetMapping("/{userId}/followees")
    public ResponseEntity<List<ReadFollowUsersDto>> readFolloweeList(@PathVariable Long userId,
                                                                     @RequestHeader(value = "X-User-Id", required = false) Long meId) {

        List<ReadFollowUsersDto> followeeList = followsService.readFolloweeList(userId, meId);

        if (followeeList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(followeeList, HttpStatus.OK);
    }

}

