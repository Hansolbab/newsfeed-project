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
                                         @RequestHeader(value = "X-User-Id", required = false) Long meId ) {


        followsService.unfollow(meId, userId);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<ReadFollowUsersDto>> readFollowerList (@PathVariable Long userId,
                                                                      @RequestHeader(value = "X-User-Id", required = false) Long meId) {

        List<ReadFollowUsersDto> followerList = followsService.readFollowerList(meId, userId);

        if(followerList.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return new ResponseEntity<>( followerList , HttpStatus.OK);
    }
}
