package com.example.newsfeedproject.feeds.controller;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.feeds.service.FeedsLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedsLikeController {
    private final FeedsLikeService feedLikeService;

    @PostMapping("/{feedId}/likes")
    @Transactional
    public ResponseEntity<Boolean> likedFeed(
            @PathVariable Long feedId,
            @AuthenticationPrincipal UserDetailsImpl userDetails){

        Boolean feedIsLiked = feedLikeService.feedLike(feedId, userDetails);

        return new ResponseEntity<>(feedIsLiked, HttpStatus.OK);
    }

    // 게시글 좋아요 수 조회
    @GetMapping("/{feedId}/likesCount")
    @Transactional(readOnly = true)
    public ResponseEntity<Long> likesCount(@PathVariable Long feedId){
        Long feedLikesTotal = feedLikeService.feedLikeCount(feedId);

        return new ResponseEntity<>(feedLikesTotal, HttpStatus.OK);
    }
}
