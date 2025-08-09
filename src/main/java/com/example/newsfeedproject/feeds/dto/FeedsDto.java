package com.example.newsfeedproject.feeds.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeedsDto {
    private Long id;
    private Long userId;
    private String userName;
    private String email;
    private String title;
    private String contents;
    private LocalDateTime created_at;
}
