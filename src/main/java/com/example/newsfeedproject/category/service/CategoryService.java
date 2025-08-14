package com.example.newsfeedproject.category.service;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.category.entity.Category;
import com.example.newsfeedproject.comment.repository.CommentsRepository;
import com.example.newsfeedproject.feeds.dto.ReadFeedsResponseDto;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.follow.repository.FollowsRepository;
import com.example.newsfeedproject.likes.repository.LikesRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService {
    private final FeedsRepository feedsRepository;
    private final LikesRepository likesRepository;
    private final CommentsRepository commentsRepository;
    private final FollowsRepository followsRepository;

    @Transactional(readOnly = true)
    public Page<ReadFeedsResponseDto> readFeedByCategory(UserDetailsImpl userDetails,
                                                     String text ,
                                                     Pageable pageable
    ){
        Long meId = userDetails.getUserId();

        Page<Feeds> feedsPage = feedsRepository.findByCategory(Category.sortedType(text), pageable);

        Set<Long> pageFeedIdSet = feedsPage.getContent().stream()//페이지에서
                .map(Feeds::getFeedId) // 아이디만 추출해서
                .collect(Collectors.toSet()); //Set으로 저장

        List<Long> feedIdsList = feedsPage.getContent().stream().map(Feeds::getFeedId).toList();

        Set<Long> likedIdSet = pageFeedIdSet.isEmpty() // feedIdSet 이 0일 경우
                ? Collections.emptySet() // 빈 셋을 반환 : DB 에러 방지
                : likesRepository.findLikedFeedIds(meId, pageFeedIdSet); // page에서 내가 좋아하는 게시글 식별자
        Map<Long, Integer> likesTotalMap =likesRepository.countLikedByFeedIds(likesRepository.findLikesByFeedId(meId).stream().toList()).stream()
                .collect(Collectors.toMap(row ->(Long) row[0], row ->((Long) row[1]).intValue()));

        Map<Long, Integer> commentsTotal = commentsRepository.countCommentsByFeedIds(feedIdsList).stream()
                .collect(Collectors.toMap(row ->(Long) row[0],
                        row ->((Long) row[1]).intValue()));


        Set<Long> followedUserIdSet = followsRepository.findFolloweeIdsByMe(meId);




        return  feedsPage.map(feeds -> ReadFeedsResponseDto.toDto(feeds,
                likedIdSet.contains(feeds.getFeedId()),
                likesTotalMap.getOrDefault(feeds.getFeedId(), 0),
                commentsTotal.getOrDefault(feeds.getFeedId(),0),
                followedUserIdSet.contains(feeds.getUser().getUserId())));
    }
    }

