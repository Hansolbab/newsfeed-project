package com.example.newsfeedproject.comment.dto;

import com.example.newsfeedproject.comment.entity.Comments;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDto {
    private Long commentId;
    private String userName;
    private String profileImageUrl;
    private String contents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponseDto(Comments comment) {
        this.commentId = comment.getCommentId();
        this.userName = comment.getUserComments().getUserName();
        this.profileImageUrl = comment.getUserComments().getProfileImageUrl();
        this.contents = comment.getContents();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}