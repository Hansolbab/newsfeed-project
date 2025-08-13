package com.example.newsfeedproject.comment.service;

import com.example.newsfeedproject.comment.dto.CommentResponseDto;
import com.example.newsfeedproject.comment.dto.CreateCommentRequestDto;
import com.example.newsfeedproject.comment.dto.UpdateCommentRequestDto;
import com.example.newsfeedproject.comment.entity.Comments;
import com.example.newsfeedproject.comment.repository.CommentsRepository;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final FeedsRepository feedsRepository;
    private final UsersRepository usersRepository;

    // 댓글 생성
    @Transactional
    public CommentResponseDto createComment(Long feedId, CreateCommentRequestDto requestDto, String userEmail) {
        // 1. 게시글 존재 여부 확인
        Feeds feed = feedsRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 2. 사용자 정보 조회
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 3. Comments 엔티티 생성
        Comments comment = Comments.builder()
                .contents(requestDto.getContents())
                .userComments(user)
                .feedComments(feed)
                .build();

        // 4. 저장
        commentsRepository.save(comment);

        // 5. Response DTO로 변환하여 반환
        return new CommentResponseDto(comment);
    }

    // 특정 게시글의 모든 댓글 조회
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getCommentsByFeed(Long feedId, Pageable pageable) {

        Feeds feed = feedsRepository.findByFeedIdAndDeletedFalse(feedId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Page<Comments> commentsPage = commentsRepository.findByFeedComments(feed, pageable);

        return commentsPage.map(CommentResponseDto::new);
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long feedId, Long commentId, UpdateCommentRequestDto requestDto, String userEmail) {
        // 1. 게시글 존재 여부 확인
        Feeds feed = feedsRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 2. 사용자 정보 조회
        Users currentUser = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 3. 댓글 존재 여부 확인 및 작성자 일치 여부 확인
        Comments comment = commentsRepository.findByCommentIdAndFeedComments(commentId, feed)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없거나 이미 삭제된 댓글입니다."));

        if (!comment.getUserComments().getUserId().equals(currentUser.getUserId())) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }

        // 4. 댓글 내용 업데이트
        comment.update(requestDto.getContents());

        // 5. Response DTO로 변환하여 반환
        return new CommentResponseDto(comment);
    }

    // 댓글 삭제 (소프트 삭제)
    @Transactional
    public void deleteComment(Long feedId, Long commentId, String userEmail) {
        // 1. 게시글 존재 여부 확인
        Feeds feed = feedsRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 2. 사용자 정보 조회
        Users currentUser = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 3. 댓글 존재 여부 확인 및 작성자 일치 여부 확인
        Comments comment = commentsRepository.findByCommentIdAndFeedComments(commentId, feed)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없거나 이미 삭제된 댓글입니다."));

        if (!comment.getUserComments().getUserId().equals(currentUser.getUserId())) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }

        // 4. 댓글 소프트 삭제 처리
        comment.softDelete();
    }
}
