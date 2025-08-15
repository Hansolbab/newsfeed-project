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
import org.springframework.data.domain.PageImpl;
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
    public Page<ReadFeedsResponseDto> readFeedByCategory(
            UserDetailsImpl userDetails,
            String text ,
            Pageable pageable
    ){
        Long meId = userDetails.getUserId();

        Page<Feeds> feedsPage = feedsRepository.findAccessibleFeedsBasedOnProfile(meId,pageable);

        Set<Long> feedIdContainCategorySet = feedsRepository.findFeedIdsByCategory(Category.sortedType(text));


        List<Feeds> pageFeedList = feedsPage.getContent().stream().filter(feeds -> feedIdContainCategorySet.contains(feeds.getFeedId()))
                .collect(Collectors.toList());

        List<Long> pageFeedIdList = pageFeedList.stream().map(Feeds::getFeedId).toList();


        Set<Long> likedIdSet = pageFeedList.isEmpty()
                ? Collections.emptySet()
                : likesRepository.findLikedFeedIds(meId, pageFeedIdList);

        Map<Long, Integer> likesTotalMap =likesRepository.countLikedByFeedIds(likesRepository.findLikesByFeedId(meId).stream().toList()).stream()
                .collect(Collectors.toMap(row ->(Long) row[0], row ->((Long) row[1]).intValue()));

        Map<Long, Integer> commentsTotalMap = commentsRepository.countCommentsByFeedIds(pageFeedIdList).stream()
                .collect(Collectors.toMap(row ->(Long) row[0],
                        row ->((Long) row[1]).intValue()));

        Set<Long> followedUserIdSet = followsRepository.findFolloweeIdsByMe(meId);

        Page<Feeds> resultFeedPage= new PageImpl<>(
                pageFeedList,
                pageable,
                pageFeedList.size()
                );

        return  resultFeedPage.map(feeds -> ReadFeedsResponseDto.toDto(feeds,
                likedIdSet.contains(feeds.getFeedId()),
                likesTotalMap.getOrDefault(feeds.getFeedId(), 0),
                commentsTotalMap.getOrDefault(feeds.getFeedId(),0),
                followedUserIdSet.contains(feeds.getUser().getUserId())));
    }
    }

