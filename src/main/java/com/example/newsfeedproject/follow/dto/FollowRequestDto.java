package com.example.newsfeedproject.follow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class FollowRequestDto {

   @NotNull
   private final Boolean folloed;
}
