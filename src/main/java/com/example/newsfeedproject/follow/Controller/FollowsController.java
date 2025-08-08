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


    @PostMapping("/{userId}")
    public ResponseEntity<Void> follow(@PathVariable Long userId,
                                        @RequestHeader(value = "X-User-Id", required = false) Long meId
    ) {

        followsService.follow(meId, userId);


        return new ResponseEntity<>(HttpStatus.OK);
    }
}
