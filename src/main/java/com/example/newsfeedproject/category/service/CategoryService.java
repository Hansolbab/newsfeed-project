package com.example.newsfeedproject.category.service;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.category.entity.Category;
import com.example.newsfeedproject.feeds.dto.FeedsResponseDto;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.likes.repository.LikesRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService {
    private final FeedsRepository feedsRepository;
    private final LikesRepository likesRepository;

    @Transactional(readOnly = true)
    public Page<FeedsResponseDto> readFeedByCategory(UserDetailsImpl userDetails,
                                                     String text ,
                                                     Pageable pageable
    ){
        Long meId = userDetails.getUserId();
        // 해당 카테고리 모든 피드 조회
        Page<Feeds> feedsPage = feedsRepository.findByCategory(Category.sortedType(text), pageable);

        // 패이지에서 피드Id만 저장
        Set<Long> pageFeedIdSet = feedsPage.getContent().stream()//페이지에서
                .map(Feeds::getFeedId) // 아이디만 추출해서
                .collect(Collectors.toSet()); //Set으로 저장

        //Page 에 있는 게시물 중 내가 좋아요한 게시물 ID 값
        Set<Long> likedIdSet = pageFeedIdSet.isEmpty() // feedIdSet 이 0일 경우
                ? Collections.emptySet() // 빈 셋을 반환 : DB 에러 방지
                : likesRepository.findLikedFeedIds(meId, pageFeedIdSet); // page에서 내가 좋아하는 게시글 식별자

        return feedsPage.map(feeds -> FeedsResponseDto.toDto(feeds, likedIdSet.contains(feeds.getFeedId())));
    }
}
