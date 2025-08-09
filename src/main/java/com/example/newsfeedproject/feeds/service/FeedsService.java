package com.example.newsfeedproject.feeds.service;

import com.example.newsfeedproject.feeds.dto.FeedsDto;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedsService {

    private final FeedsRepository feedsRepository;

    private FeedsDto mapToFeedsDto(Feeds feeds) {
        return FeedsDto.builder()
                .userName(feeds.getUserName())
                .title(feeds.getTitle())
                .contents(feeds.getContents())
                .created_at(feeds.getCreated_at())
                .updated_at(feeds.getUpdated_at())
                .build();
    }

    public List<FeedsDto> findAll() {
        return feedsRepository.findAll().stream().map(this::mapToFeedsDto).toList();
    }
}


