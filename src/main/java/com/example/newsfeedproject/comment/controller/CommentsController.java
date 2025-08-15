package com.example.newsfeedproject.comment.controller;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.comment.dto.CommentResponseDto;
import com.example.newsfeedproject.comment.dto.CreateCommentRequestDto;
import com.example.newsfeedproject.comment.dto.UpdateCommentRequestDto;
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

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class CommentsController {
    private final CommentsService commentsService;

    // 댓글 생성 API
    @PostMapping("/{feedId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long feedId,
            @Valid @RequestBody CreateCommentRequestDto createCommentRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ){
        String userEmail = userDetailsImpl.getUsername();
        // 서비스 계층을 통해 댓글 생성 비즈니스 로직 수행
        CommentResponseDto responseDto = commentsService.createComment(feedId, createCommentRequestDto, userEmail);

        // HTTP 201 Created 상태 코드와 함께 생성된 댓글 정보 DTO 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 댓글 조회 API
    @GetMapping("/{feedId}/comments")
    public ResponseEntity<Page<CommentResponseDto>> readCommentsByFeed(
            @PathVariable Long feedId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ){
        // 정렬 기준 추가 (최신순)
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort); // Pageable 객체 생성

        // 서비스 계층을 통해 특정 게시글의 댓글 목록 조회 (Pageable 전달)
        Page<CommentResponseDto> responseDtoPage = commentsService.readCommentsByFeed(feedId, pageable); // Pageable 전달

        return ResponseEntity.ok(responseDtoPage);
    }

    // 댓글 수정 API
    @PutMapping("/{feedId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long feedId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequestDto updateCommentRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ){
        String userEmail = userDetailsImpl.getUsername();
        CommentResponseDto responseDto = commentsService.updateComment(feedId, commentId, updateCommentRequestDto, userEmail);

        return ResponseEntity.ok(responseDto);
    }

    // 댓글 삭제 API (DELETE /api/feeds/{feedId}/comments/{commentId})
    @DeleteMapping("/{feedId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long feedId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ){
        String userEmail = userDetailsImpl.getUsername();
        commentsService.deleteComment(feedId, commentId, userEmail);

        return ResponseEntity.noContent().build();
    }
}
