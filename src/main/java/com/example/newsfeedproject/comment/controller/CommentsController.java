package com.example.newsfeedproject.comment.controller;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.comment.dto.CommentResponseDto;
import com.example.newsfeedproject.comment.dto.CreateCommentRequestDto;
import com.example.newsfeedproject.comment.service.CommentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feeds/{feedId}/comments")
@RequiredArgsConstructor
public class CommentsController {

    private final CommentsService commentsService;

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

}
