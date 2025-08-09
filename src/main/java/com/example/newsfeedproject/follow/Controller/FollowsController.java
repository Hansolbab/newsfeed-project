package com.example.newsfeedproject.follow.Controller;


import com.example.newsfeedproject.follow.service.FollowsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
}
