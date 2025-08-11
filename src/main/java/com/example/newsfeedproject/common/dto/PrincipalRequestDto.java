package com.example.newsfeedproject.common.dto;

import lombok.Getter;

import java.util.Set;

@Getter
public class PrincipalRequestDto {
    private Long userId;
    private Set<String> authorities; // Set 사용이유 중복된값 없애고 빠르게 검색하기 위해서

    public PrincipalRequestDto(Long userId, Set<String> authorities) {
        this.userId = userId;
        if (authorities.isEmpty()){ this.authorities = Set.of(); }
        else { this.authorities = Set.copyOf(authorities); }
    }
}
