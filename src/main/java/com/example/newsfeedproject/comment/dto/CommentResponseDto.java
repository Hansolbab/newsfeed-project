package com.example.newsfeedproject.comment.dto;

import com.example.newsfeedproject.comment.entity.Comments;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDto {

    private Long commentId;
    private UserInfo user;
    private String contents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponseDto(Comments comment) {
        this.commentId = comment.getCommentId();
        // userComments 필드에 접근하여 UserInfo 생성 (이전 Users 엔티티에 맞춰 getter 사용)
        this.user = new UserInfo(comment.getUserComments().getUserName(), comment.getUserComments().getProfileImageUrl());
        this.contents = comment.getContents();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }

    @Getter
    @Setter
    public static class UserInfo {
        private String userName;        // 작성자 이름
        private String profileImageUrl; // 작성자 프로필 이미지 URL

        public UserInfo(String userName, String profileImageUrl) {
            this.userName = userName;
            this.profileImageUrl = profileImageUrl;
        }
    }
}