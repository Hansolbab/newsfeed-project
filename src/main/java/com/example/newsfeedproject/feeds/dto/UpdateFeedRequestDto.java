package com.example.newsfeedproject.feeds.dto;

import com.example.newsfeedproject.category.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateFeedRequestDto {

    // 게시글 내용 (필수, 길이 제한)
    @NotBlank(message = "게시글 내용은 필수 입력 항목입니다.")
    @Size(max = 2000, message = "게시글 내용은 최대 2000자까지 허용됩니다.")
    private String contents;

    // 게시글 이미지 URL 목록 (클라이언트로부터 String URL 목록을 받음)
    @NotNull(message = "게시글 이미지는 필수입니다.")
    @Size(min = 1, message = "게시글 이미지는 최소 1개 이상이어야 합니다.")
    private List<String> feedImageUrls;

    // 게시글 카테고리 (필수)
    @NotNull(message = "게시글 카테고리는 필수 선택 항목입니다.")
    private Category category; // Category Enum 타입으로 요청 받음
}