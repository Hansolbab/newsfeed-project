package com.example.newsfeedproject.comment.controller;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.comment.dto.CommentResponseDto;
import com.example.newsfeedproject.comment.dto.CreateCommentRequestDto;
import com.example.newsfeedproject.comment.entity.Comments;
import com.example.newsfeedproject.comment.service.CommentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feeds/{feedId}/comments")
@RequiredArgsConstructor
public class CommentsController {

    private final CommentsService commentsService;

    // 댓글 생성 API
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long feedId,
            @Valid @RequestBody CreateCommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {
        // userDetailsImpl에서 사용자 이메일(식별자) 추출
        String userEmail = userDetailsImpl.getUsername();

        // 서비스 계층을 통해 댓글 생성 비즈니스 로직 수행
        CommentResponseDto responseDto = commentsService.createComment(feedId, requestDto, userEmail);

        // HTTP 201 Created 상태 코드와 함께 생성된 댓글 정보 DTO 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    // 댓글 조회 API
    @GetMapping
    public ResponseEntity<Page<CommentResponseDto>> getCommentsByFeed(
            @PathVariable Long feedId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {

        // 정렬 기준 추가 (최신순)
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort); // Pageable 객체 생성

        // 서비스 계층을 통해 특정 게시글의 댓글 목록 조회 (Pageable 전달)
        Page<CommentResponseDto> responseDtoPage = commentsService.getCommentsByFeed(feedId, pageable); // Pageable 전달

        return ResponseEntity.ok(responseDtoPage);
    }

}
