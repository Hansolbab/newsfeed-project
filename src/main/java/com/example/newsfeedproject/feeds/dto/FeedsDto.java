package com.example.newsfeedproject.feeds.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedsDto {
    private Long id;
    private Long userId;
    private String userName;
    private String email;
    private String title;
    private String contents;
    private Data created;
    private Data updated;
}
