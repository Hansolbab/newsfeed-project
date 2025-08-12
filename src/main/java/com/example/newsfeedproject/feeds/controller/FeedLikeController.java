package com.example.newsfeedproject.feeds.controller;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.feeds.service.FeedLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedLikeController {
    private final FeedLikeService feedLikeService;

    @PostMapping("/{feedId}/likes")
    @Transactional
    public ResponseEntity<Boolean> likedFeed(
            @PathVariable Long feedId,
            @AuthenticationPrincipal UserDetailsImpl userDetails){

        Boolean feedIsLiked = feedLikeService.feedLike(feedId, userDetails);

        return new ResponseEntity<>(feedIsLiked, HttpStatus.OK);
    }
}
