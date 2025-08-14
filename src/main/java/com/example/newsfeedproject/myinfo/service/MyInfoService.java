package com.example.newsfeedproject.myinfo.service;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.comment.repository.CommentsRepository;
import com.example.newsfeedproject.common.exception.users.UsersErrorException;
import com.example.newsfeedproject.feeds.dto.FeedsResponseDto;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.likes.repository.LikesRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static com.example.newsfeedproject.common.exception.users.UsersErrorCode.*;
@Service
@AllArgsConstructor
public class MyInfoService {
    private final FeedsRepository feedsRepository;
    private final LikesRepository likesRepository;
    private final CommentsRepository commentsRepository;

    public Page<FeedsResponseDto> readFeedsByMyComment(UserDetailsImpl userDetails, Pageable pageable) {
        Long meId = userDetails.getUserId();

        Page<Feeds> feedsPage = feedsRepository.findFeedsByCommentsBy(meId,pageable);

        List<Long> feedIdList = feedsPage.getContent().stream().map(Feeds::getFeedId).toList();

        Set<Long> likedIdSet = feedIdList.isEmpty() // feedIdSet 이 0일 경우
                ? Set.of() // 빈 셋을 반환 : DB 에러 방지
                : likesRepository.findLikedFeedIds(meId, feedIdList);

        Map<Long, Integer> likeTotalMap =likesRepository.countLikedByFeedIds(likesRepository.findLikesByFeedId(meId).stream().toList()).stream()
                .collect(Collectors.toMap(row ->(Long) row[0], row ->((Long) row[1]).intValue()));

        Map<Long, Integer> commentTotalMap = commentsRepository.countCommentsByFeedIds(feedIdList).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> ((Long) row[1]).intValue()));

        return  feedsPage.map(feeds -> FeedsResponseDto.toDto(feeds, likedIdSet.contains(feeds.getFeedId()),likeTotalMap.getOrDefault(feeds.getFeedId(),0), commentTotalMap.getOrDefault(feeds.getFeedId(), 0)));
    }

    public Page<FeedsResponseDto> readFeedsByMyLikes(UserDetailsImpl userDetails, Pageable pageable) {
        if(userDetails == null) {
            throw new UsersErrorException(NOT_A_USER);
        }

        Long meId =  userDetails.getUserId();

        Set<Long> likesIdSet =  likesRepository.findLikesByFeedId(meId);


        Map<Long, Integer> likeTotalMap =likesRepository.countLikedByFeedIds(likesRepository.findLikesByFeedId(meId).stream().toList()).stream()
                .collect(Collectors.toMap(row ->(Long) row[0], row ->((Long) row[1]).intValue()));

        return (likesIdSet.isEmpty()) ? Page.empty(pageable)
                :feedsRepository.findByIdIn(likesIdSet, pageable)
                .map(feeds ->

                        FeedsResponseDto.toDto(feeds, true, likeTotalMap.getOrDefault(feeds.getFeedId(),0), likeTotalMap.getOrDefault(feeds.getFeedId(),0)));
    }
}