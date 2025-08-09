package com.example.newsfeedproject.feeds.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedUpdateRequestDto {
    private String content;
    private String feedImgs;

    @Pattern(regexp = "KOREAN|JAPANESE|CHINESE|WESTERN|OTHERS",
            message = "Invalid category. Valid categories are KOREAN, JAPANESE, CHINESE, WESTERN, OTHERS.")
    private String category;
}