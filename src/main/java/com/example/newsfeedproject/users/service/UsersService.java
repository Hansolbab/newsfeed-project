package com.example.newsfeedproject.users.service;

import com.example.newsfeedproject.comment.repository.CommentsRepository;
import com.example.newsfeedproject.common.dto.PrincipalRequestDto;
import com.example.newsfeedproject.common.dto.ReadUserSimpleResponseDto;
import com.example.newsfeedproject.feedimg.repository.FeedImgRepository;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.likes.repository.LikesRepository;
import com.example.newsfeedproject.users.dto.ReadUsersFeedsResponseDto;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UsersService {

    @Autowired
    private final UsersRepository usersRepository;
    @Autowired
    private final FeedsRepository feedsRepository;
    @Autowired
    private final LikesRepository likeRepository;
    @Autowired
    private final CommentsRepository commentsRepository;
    @Autowired
    private final FeedImgRepository feedImgRepository;

    public ReadUserSimpleResponseDto readUserSimple(Long userId, PrincipalRequestDto principalRequestDto) {
        // userId(프로필 보려는 대상 userId) null 확인
        Optional<Users> user = usersRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("없는 유저입니다.");
        }
        // 본인 프로필 확인할때
//        if (principalRequestDto.getUserId() == userId) {
//          현재는 반환하는 값이 다른게 없어서 그대로 진행
//        }

        Users userSimple = user.get();
        return new ReadUserSimpleResponseDto(userSimple.getUserName(),userSimple.getProfileImageUrl());
    }


    public Page<ReadUsersFeedsResponseDto> readUserFeed(Long userId, PrincipalRequestDto principalRequestDto) {
        Optional<Users> user = usersRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("없는 유저입니다.");
        }

        Pageable pageable = PageRequest.of(0, 10,  Sort.by(Sort.Direction.DESC, "createdAt") );


        Page<Feeds> feeds = feedsRepository.findByUser_UserId(userId, pageable);  // userId에 맞는 Feed들 Page로 받음
        List<Long> feedIds = feeds.stream().map(Feeds::getFeedId).toList(); // Feed에서 Id값만 리스트로 정리

        //null 값 때문에 Object로 받고 Map으로 key: feedId, value: count한 like값
        Map<Long, Integer> likeTotal =likeRepository.countLikedByFeedIds(feedIds).stream()
                                        .collect(Collectors.toMap(row ->(Long) row[0], row ->(Integer) row[1]));

        //null 값 때문에 Object로 받고 Map으로 Key: feedId, value: count한 comments값
        Map<Long, Integer> commentsTotal = commentsRepository.countCommentsByFeedIds(feedIds).stream()
                                            .collect(Collectors.toMap(row ->(Long) row[0], row ->(Integer) row[1]));


        // null 값 때문에 Object로 받고 Map으로 Key: feedId, value: liked true값
        Map<Long, Boolean> liked = likeRepository.isLikedByFeedIdsANDUserId(feedIds, userId).stream()
                                        .collect(Collectors.toMap(row->(Long) row[0], row ->(Boolean) row[1]));


        Map<Long, List<String>> feedImgs = feedImgRepository.findFeedImgByFeedId(feedIds).stream()
                        .collect(Collectors.groupingBy(row -> (Long) row[0], // feedId를 기준으로 그룹화
                                 Collectors.mapping(row -> (String) row[1], // feedImageUrl만 뽑아서
                                 Collectors.toList() // 리스트로 묶음
                        )));

        List<ReadUsersFeedsResponseDto> feedResponseDtoList = feeds.stream()
                .map(feed -> new ReadUsersFeedsResponseDto(
                        feed.getFeedId(),                                               // FeedId 값
                        feedImgs.get(feed.getFeedId()),                                 // FeedImageUrl List값
                        feed.getContents(),                                             // Contents 값
                        likeTotal.getOrDefault(feed.getFeedId(),0),           // likeTotal 값, 기본 0
                        commentsTotal.getOrDefault(feed.getFeedId(), 0),      // commentsTotal 값, 기본 0
                        liked.getOrDefault(feed.getFeedId(), false)         // liked 값, 기본 false
                        ))
                .toList();

        return new PageImpl<>(feedResponseDtoList, pageable, feeds.getTotalElements());
    }

}
