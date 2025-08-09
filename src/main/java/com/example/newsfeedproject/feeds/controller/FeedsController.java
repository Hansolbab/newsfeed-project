package com.example.newsfeedproject.feeds.controller;

import com.example.newsfeedproject.feeds.dto.*;
import com.example.newsfeedproject.feeds.service.FeedsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.newsfeedproject.common.exception.SizeLimitExceededException;

import java.util.List;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedsController {

    private final FeedsService feedsService;

    // --- 게시글 생성 (CREATE) API ---
    @PostMapping
    public ResponseEntity<FeedResponseDto> createFeed(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody FeedCreateRequestDto requestDto) {

        try {
            FeedResponseDto newFeed = feedsService.createFeed(requestDto, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(newFeed);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (SizeLimitExceededException e) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, e.getMessage());
        }
    }

    // --- 게시글 단건 조회 (READ One) API ---
    @GetMapping("/{feedId}")
    public ResponseEntity<FeedResponseDto> getFeedById(
            @PathVariable Long feedId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            FeedResponseDto feeds = feedsService.getFeedById(feedId, userDetails.getUsername());
            return ResponseEntity.ok(feeds);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }

    // --- 전체 피드 조회 (READ All) API ---
    @GetMapping
    public ResponseEntity<List<FeedResponseDto>> getAllFeeds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<FeedResponseDto> feeds = feedsService.getAllFeeds(page, size, userDetails.getUsername());
        return ResponseEntity.ok(feeds);
    }

    // --- 게시글 수정 (UPDATE) API ---
    @PutMapping("/{feedId}")
    public ResponseEntity<FeedResponseDto> updateFeed(
            @PathVariable Long feedId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody FeedUpdateRequestDto requestDto) {

        try {
            FeedResponseDto updatedFeeds = feedsService.updateFeed(feedId, requestDto, userDetails.getUsername());
            return ResponseEntity.ok(updatedFeeds);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update feeds", e);
        }
    }

    // --- 게시글 삭제 (DELETE) API ---
    @DeleteMapping("/{feedId}") // HTTP DELETE 요청
    public ResponseEntity<MessageResponseDto> deleteFeed(
            @PathVariable Long feedId, // URL 경로에서 feedId 추출
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            // Service 계층의 deleteFeed 메서드 호출
            feedsService.deleteFeed(feedId, userDetails.getUsername());
            // 명세서에 맞춰 200 OK에 메시지를 반환
            return ResponseEntity.ok(new MessageResponseDto("정상적으로 삭제되었습니다."));
        } catch (ResponseStatusException e) { // 서비스 계층에서 발생한 권한/찾을 수 없음 등 예외 처리
            throw e;
        } catch (Exception e) { // 예상치 못한 내부 서버 오류
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete feeds", e);
        }
    }
}