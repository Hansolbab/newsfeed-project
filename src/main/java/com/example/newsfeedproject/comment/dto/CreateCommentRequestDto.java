package com.example.newsfeedproject.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequestDto {
    @NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
    @Size(max = 500, message = "댓글 내용은 최대 500자까지 허용됩니다.")
    private String contents;
}