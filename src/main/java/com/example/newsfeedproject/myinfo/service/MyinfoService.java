package com.example.newsfeedproject.myinfo.service;


import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.feeds.dto.FeedResponseDto;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.likes.repository.LikesRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
@AllArgsConstructor
public class MyinfoService {

    private final FeedsRepository feedsRepository;
    private final LikesRepository likesRepository;

    public Page<FeedResponseDto> readFeedsByMyCommnet(UserDetailsImpl userDetails, Pageable pageable) {

        Long meId = userDetails.getUserId();

        Page<Feeds> feedsPage = feedsRepository.findFeedsByCommentsBy(meId,pageable);

        List<Long> feedIdList = feedsPage.getContent().stream().map(Feeds::getFeedId).toList();

        Set<Long> likedIdSet = feedIdList.isEmpty() // feedIdSet 이 0일 경우
                ? Set.of() // 빈 셋을 반환 : DB 에러 방지
                : likesRepository.findLikedFeedIds(meId, feedIdList);


        return  feedsPage.map(feeds -> FeedResponseDto.toDto(feeds, likedIdSet.contains(feeds.getFeedId())));
    }
}