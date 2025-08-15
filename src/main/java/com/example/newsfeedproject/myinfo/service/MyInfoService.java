package com.example.newsfeedproject.myinfo.service;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.comment.repository.CommentsRepository;
import static com.example.newsfeedproject.common.exception.auth.AuthErrorCode.*;
import com.example.newsfeedproject.common.exception.auth.AuthErrorException;
import com.example.newsfeedproject.common.exception.users.UsersErrorException;
import com.example.newsfeedproject.feeds.dto.ReadFeedsResponseDto;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.follow.repository.FollowsRepository;
import com.example.newsfeedproject.likes.repository.LikesRepository;
import com.example.newsfeedproject.myinfo.dto.AccessAbleDto;
import com.example.newsfeedproject.users.entity.AccessAble;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static com.example.newsfeedproject.common.exception.users.UsersErrorCode .*;
@Service
@AllArgsConstructor
public class MyInfoService {
    private final FeedsRepository feedsRepository;
    private final LikesRepository likesRepository;
    private final CommentsRepository commentsRepository;
    private final FollowsRepository followsRepository;
    private final UsersRepository usersRepository;

    public Page<ReadFeedsResponseDto> readFeedsByMyLikes(UserDetailsImpl userDetails, Pageable pageable) {

        Long meId = userDetails.getUserId();


        Page<Feeds> feedsPage =  feedsRepository.findAccessibleFeedsBasedOnProfile(meId,pageable);

        List<Long> feedIdsList = feedsPage.getContent().stream().map(Feeds::getFeedId).toList();


        Set<Long> likedIdSet = feedIdsList.isEmpty() // feedIdSet 이 0일 경우
                ? Set.of() // 빈 셋을 반환 : DB 에러 방지
                : likesRepository.findLikedFeedIds(meId, feedIdsList);


        Map<Long, Integer> likesTotalMap = likesRepository.countLikedByFeedIds(feedIdsList).stream()
                .collect(Collectors.toMap(row ->(Long) row[0],
                row ->((Long) row[1]).intValue()));

        Map<Long, Integer> commentsTotalMap = commentsRepository.countCommentsByFeedIds(feedIdsList).stream()
                .collect(Collectors.toMap(row ->(Long) row[0],
                        row ->((Long) row[1]).intValue()));


        List<Feeds> filteredLikes = feedsPage.stream().filter(feeds -> likedIdSet.contains(feeds.getFeedId())).toList();

        Set<Long> followedUserIdSet = followsRepository.findFolloweeIdsByMe(meId);

        Page<Feeds> filteredPage = new PageImpl<>(
                filteredLikes,
                pageable,
                filteredLikes.size()
        );
        return  filteredPage.map(feeds -> ReadFeedsResponseDto.toDto(feeds,
                likedIdSet.contains(feeds.getFeedId()),
                likesTotalMap.getOrDefault(feeds.getFeedId(), 0),
                commentsTotalMap.getOrDefault(feeds.getFeedId(),0),
                followedUserIdSet.contains(feeds.getUser().getUserId())));
    }

    public Page<ReadFeedsResponseDto> readFeedsByMyComment(UserDetailsImpl userDetails, Pageable pageable) {

        if (userDetails == null) {
            throw new UsersErrorException(NOT_A_USER);
        }

        Long meId = userDetails.getUserId();

        Page<Feeds> feedsPage =  feedsRepository.findAccessibleFeedsBasedOnProfile(meId,pageable);

        List<Long> feedIdsList = feedsPage.stream().map(Feeds::getFeedId).toList();

        Set<Long> commentsIdSet = feedIdsList.isEmpty() // feedIdSet 이 0일 경우
                ? Set.of() // 빈 셋을 반환 : DB 에러 방지
                : commentsRepository.findCommentsFeedIds(meId, feedIdsList);

        Set<Long> likesIdSet = likesRepository.findLikesByFeedId(meId);

        Map<Long, Integer> likesTotalMap = likesRepository.countLikedByFeedIds(feedIdsList.stream().toList()).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> ((Long) row[1]).intValue()));

        Map<Long, Integer> commentsTotalMap = commentsRepository.countCommentsByFeedIds(feedIdsList).stream()
                .collect(Collectors.toMap(row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()));

        Set<Long> followedUserIdSet = followsRepository.findFolloweeIdsByMe(meId);

        List<Feeds> filteredContent = feedsPage.stream().filter(feeds -> commentsIdSet.contains(feeds.getFeedId())).toList();

        Page<Feeds> filteredPage = new PageImpl<>(
                filteredContent,
                pageable,
                filteredContent.size()
        );

        return filteredPage
                .map(feeds -> ReadFeedsResponseDto.toDto(feeds,
                    likesIdSet.contains(feeds.getFeedId()),
                    likesTotalMap.getOrDefault(feeds.getFeedId(), 0),
                    commentsTotalMap.getOrDefault(feeds.getFeedId(),0),
                    followedUserIdSet.contains(feeds.getUser().getUserId())));


    }

    public AccessAble accessAbleMyPage(UserDetailsImpl userDetails, AccessAbleDto accessAble) {
        Users user = usersRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new AuthErrorException(USER_NOT_FOUND));
        user.setVisibility(accessAble.getAccessAble());
        return user.getVisibility();
    }


}