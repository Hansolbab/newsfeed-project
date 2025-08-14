package com.example.newsfeedproject.feeds.controller;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.category.entity.Category;
import com.example.newsfeedproject.feeds.dto.CreateFeedsRequestDto;
import com.example.newsfeedproject.feeds.dto.CreateFeedResponseDto;
import com.example.newsfeedproject.feeds.dto.FeedsResponseDto;
import com.example.newsfeedproject.feeds.dto.UpdateFeedsRequestDto;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.service.FeedsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedsController {
    private final FeedsService feedsService;

    // 게시글 작성 API (POST /api/feeds)
    @PostMapping
    public ResponseEntity<CreateFeedResponseDto> createFeed(
            @Valid @RequestBody CreateFeedsRequestDto createFeedsRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {

        // 서비스 계층을 통해 게시글 생성 비즈니스 로직 수행
        Feeds createdFeeds = feedsService.createFeed(createFeedsRequestDto, userDetailsImpl);

        // HTTP 201 Created 상태 코드와 함께 생성된 게시글 정보 DTO 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateFeedResponseDto(createdFeeds));
    }

    // 게시글 전체 조회 API (GET /api/feeds)
    // 페이징 처리: page (페이지 번호, 0부터 시작), size (페이지당 게시글 수)
    @GetMapping
    public ResponseEntity<Page<FeedsResponseDto>> readAllFeeds(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "category", required = false) String categoryString,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl // 현재 인증된 사용자 정보 주입
    ) {
        // 서비스 계층을 통해 게시글 목록 조회
        Page<FeedsResponseDto> feedsPage = feedsService.readAllFeeds(page, size, userDetailsImpl);

        // 조회된 페이지(Page 객체) 반환
        return ResponseEntity.ok(feedsPage);
    }

    // 게시글 단건 조회 API (GET /api/feeds/{feedId})
    @GetMapping("/{feedId}")
    public ResponseEntity<FeedsResponseDto> readFeedById(
            @PathVariable Long feedId, // URL 경로 변수에서 게시글 ID 추출
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl // 현재 인증된 사용자 정보 주입
    ) {
        // userDetailsImpl에서 사용자 이메일(식별자) 추출
        String userEmail = userDetailsImpl.getUsername(); // UserDetailsImpl에서 이메일 반환 가정

        // 서비스 계층을 통해 게시글 단건 조회
        FeedsResponseDto responseDto = feedsService.readFeedById(feedId, userDetailsImpl);

        // 조회된 게시글 DTO 반환
        return ResponseEntity.ok(responseDto);
    }

    // 게시글 수정 API (PUT /api/feeds/{feedId})
    @PutMapping("/{feedId}")
    public ResponseEntity<FeedsResponseDto> updateFeed(
            @PathVariable Long feedId, // URL 경로 변수에서 게시글 ID 추출
            @Valid @RequestBody UpdateFeedsRequestDto updateFeedsRequestDto, // 요청 본문 DTO, 유효성 검사
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl // 현재 인증된 사용자 정보 주입
    ) {
// userDetailsImpl에서 사용자 이메일(식별자) 추출
        String userEmail = userDetailsImpl.getUsername(); // UserDetailsImpl에서 이메일 반환 가정

        // 서비스 계층을 통해 게시글 수정 비즈니스 로직 수행
        FeedsResponseDto responseDto = feedsService.updateFeed(feedId, updateFeedsRequestDto, userDetailsImpl);

        // 수정된 게시글 DTO 반환
        return ResponseEntity.ok(responseDto);
    }

    // 게시글 삭제 API (DELETE /api/feeds/{feedId})
    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(
            @PathVariable Long feedId, // URL 경로 변수에서 게시글 ID 추출
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl // 현재 인증된 사용자 정보 주입
    ) {
        // userDetailsImpl에서 사용자 이메일(식별자) 추출
        String userEmail = userDetailsImpl.getUsername(); // UserDetailsImpl에서 이메일 반환 가정

        // 서비스 계층을 통해 게시글 삭제 비즈니스 로직 수행
        feedsService.deleteFeed(feedId, userDetailsImpl);

        // 성공 시 204 No Content 상태 코드 반환 (응답 본문 없음)
        return ResponseEntity.noContent().build();
    }

    // 소프트 삭제된 게시글 조회)

    // 1. 모든 소프트 삭제된 게시글 조회 API (GET /api/feeds/deleted)
    @GetMapping("/deleted")
    public ResponseEntity<Page<FeedsResponseDto>> readAllDeletedFeeds(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {
        String userEmail = userDetailsImpl.getUsername();
        Page<FeedsResponseDto> feedsPage = feedsService.readAllDeletedFeeds(page, size, userDetailsImpl);
        return ResponseEntity.ok(feedsPage);
    }

    // 2. 특정 소프트 삭제된 게시글 단건 조회 API (GET /api/feeds/deleted/{feedId})
    @GetMapping("/deleted/{feedId}")
    public ResponseEntity<FeedsResponseDto> getDeletedFeedById(
            @PathVariable Long feedId,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {
        String userEmail = userDetailsImpl.getUsername();
        FeedsResponseDto responseDto = feedsService.readDeletedFeedById(feedId, userDetailsImpl);
        return ResponseEntity.ok(responseDto);
    }

    // 게시글 복구 API (PUT /api/feeds/restore/{feedId})
    @PutMapping("/restore/{feedId}")
    public ResponseEntity<FeedsResponseDto> restoreFeed(
            @PathVariable Long feedId,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {
        String userEmail = userDetailsImpl.getUsername();
        FeedsResponseDto responseDto = feedsService.restoreFeed(feedId, userDetailsImpl);
        return ResponseEntity.ok(responseDto);
    }
}