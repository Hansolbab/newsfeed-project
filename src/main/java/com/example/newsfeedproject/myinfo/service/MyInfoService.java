package com.example.newsfeedproject.myinfo.service;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.comment.repository.CommentsRepository;
import com.example.newsfeedproject.common.exception.auth.AuthErrorException;
import com.example.newsfeedproject.common.exception.users.UsersErrorException;
import com.example.newsfeedproject.feeds.dto.ReadFeedsResponseDto;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.likes.repository.LikesRepository;
import com.example.newsfeedproject.myinfo.dto.AccessAbleDto;
import com.example.newsfeedproject.users.entity.AccessAble;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.newsfeedproject.common.exception.auth.AuthErrorCode.USER_NOT_FOUND;
import static com.example.newsfeedproject.common.exception.users.UsersErrorCode.*;
@Service
@AllArgsConstructor
public class MyInfoService {
    private final FeedsRepository feedsRepository;
    private final LikesRepository likesRepository;
    private final CommentsRepository commentsRepository;
    private final UsersRepository usersRepository;

    public Page<ReadFeedsResponseDto> readFeedsByMyComment(UserDetailsImpl userDetails, Pageable pageable) {
        Long meId = userDetails.getUserId();

        Page<Feeds> feedsPage = feedsRepository.findFeedsByCommentsBy(meId,pageable);

        List<Long> feedIdsList = feedsPage.getContent().stream().map(Feeds::getFeedId).toList();

        Set<Long> likedIdSet = feedIdsList.isEmpty() // feedIdSet 이 0일 경우
                ? Set.of() // 빈 셋을 반환 : DB 에러 방지
                : likesRepository.findLikedFeedIds(meId, feedIdsList);

        Map<Long, Integer> likesTotal = likesRepository.countLikedByFeedIds(feedIdsList).stream().collect(Collectors.toMap(row ->(Long) row[0],
                row ->((Long) row[1]).intValue()));

        Map<Long, Integer> commentsTotal = commentsRepository.countCommentsByFeedIds(feedIdsList).stream()
                .collect(Collectors.toMap(row ->(Long) row[0],
                        row ->((Long) row[1]).intValue()));


        return  feedsPage.map(feeds -> ReadFeedsResponseDto.toDto(feeds,
                likedIdSet.contains(feeds.getFeedId()),
                likesTotal.getOrDefault(feeds.getFeedId(), 0),
                commentsTotal.getOrDefault(feeds.getFeedId(),0)));
    }

    public Page<ReadFeedsResponseDto> readFeedsByMyLikes(UserDetailsImpl userDetails, Pageable pageable) {
        if(userDetails == null) {
            throw new UsersErrorException(NOT_A_USER);
        }

        Long meId =  userDetails.getUserId();

        Set<Long> likesIdSet =  likesRepository.findLikesByFeedId(meId);

        List<Long> feedIdsList = likesRepository.findLikesByFeedId(meId).stream().toList();

        Map<Long, Integer> likeTotalMap =likesRepository.countLikedByFeedIds(feedIdsList.stream().toList()).stream()
                .collect(Collectors.toMap(row ->(Long) row[0], row ->((Long) row[1]).intValue()));

        Map<Long, Integer> commentsTotal = commentsRepository.countCommentsByFeedIds(feedIdsList).stream()
                .collect(Collectors.toMap(row ->(Long) row[0],
                        row ->((Long) row[1]).intValue()));


        return (likesIdSet.isEmpty()) ? Page.empty(pageable)
                :feedsRepository.findByIdIn(likesIdSet, pageable)
                .map(feeds ->

                        ReadFeedsResponseDto.toDto(feeds, true, likeTotalMap.getOrDefault(feeds.getFeedId(),0),commentsTotal.getOrDefault(feeds.getFeedId(),0)));
    }

    public AccessAble accessAlbeMyPage(UserDetailsImpl userDetails, AccessAbleDto accessAble) {
        Users user = usersRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new AuthErrorException(USER_NOT_FOUND));
        user.setVisibility(accessAble.getAccessAble());
        return user.getVisibility();
    }
}