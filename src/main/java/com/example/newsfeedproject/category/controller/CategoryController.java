package com.example.newsfeedproject.category.controller;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.category.service.CategoryService;
import com.example.newsfeedproject.feeds.dto.FeedsResponseDto;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<Page<FeedsResponseDto>> readFeedByCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam @NotNull(message = "카테고리는 필수입니다.") String category,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return new ResponseEntity<>(categoryService.readFeedByCategory(userDetails, category ,pageable), HttpStatus.OK);
    }
}
