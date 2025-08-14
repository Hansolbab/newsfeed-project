package com.example.newsfeedproject.users.service;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.comment.repository.CommentsRepository;
import com.example.newsfeedproject.common.dto.PrincipalRequestDto;
import com.example.newsfeedproject.common.dto.ReadUserSimpleResponseDto;
import com.example.newsfeedproject.feedimg.repository.FeedImgRepository;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.follow.repository.FollowsRepository;
import com.example.newsfeedproject.likes.repository.LikesRepository;
import com.example.newsfeedproject.common.dto.ReadUsersFeedsResponseDto;
import com.example.newsfeedproject.users.dto.SearchUserResponseDto;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final FeedsRepository feedsRepository;
    private final LikesRepository likeRepository;
    private final CommentsRepository commentsRepository;
    private final FeedImgRepository feedImgRepository;
    private final FollowsRepository followsRepository;

    // userId 값의 User를 읽을 수 있는 권한 있을 때 Users 형태로 반환
    public Users readUserAuthority(Long userId, UserDetailsImpl userDetails){
        // 로그인 안한 경우
        PrincipalRequestDto principalUser = new PrincipalRequestDto(userDetails.getUserId(),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));   // Set으로 변환해서 반환
        if (!principalUser.getAuthorities().equals("ROLE_USER")){
            throw  new IllegalArgumentException("유저가 아닙니다.");
        }

        if (userDetails==null) {throw new IllegalStateException("로그인이 필요합니다.");}

        Optional<Users> user = usersRepository.findById(userId);
        if (user.isEmpty()) {throw new IllegalArgumentException("없는 유저입니다.");}


        // 보는 사람 : principalUser.getUserId(), 볼 사람 : userId가 다를 때 (본인 프로필이 아닐 때)
        if (!principalUser.getUserId().equals(userId)) {
            // 보는 사람 : principalUser.getUserId(), 볼 사람 : userId 팔로우가 아닐 때
            if(!followsRepository.existsByFollower_UserIdAndFollowee_UserIdAndFollowedTrue(principalUser.getUserId(), userId)){
                throw new IllegalArgumentException("팔로우 필요");
            }
        }

        return user.get();
    }

    public ReadUserSimpleResponseDto readUserSimple(Long userId, UserDetailsImpl userDetails) {
        Users userSimple = readUserAuthority(userId, userDetails);

        // 내 프로필일땐 팔로우 Null이나, 모르는 사람일땐 팔로우 여부 반환 하는 형태로 변형

        // userDetails.getUserId():보는 입장, userId: 볼 상대를 팔로우 하고 있는지 여부
        boolean isFollowed = followsRepository.existsByFollower_UserIdAndFollowee_UserIdAndFollowedTrue(userDetails.getUserId(), userId);
        return new ReadUserSimpleResponseDto(userSimple.getUserName(),userSimple.getProfileImageUrl(), isFollowed);
    }


    public Page<ReadUsersFeedsResponseDto> readUserFeed(
            Long userId,
            UserDetailsImpl userDetails,
            Pageable pageable
    ){
        Users user = readUserAuthority(userId, userDetails);
        // 추가사항 확인 하고 반환값 설정
        // 본인 페이지 or 내가 상대를 Follow한 상태일때 피드 정보 값
        // 현재는 두개의 값이 다른점이 없어서 하나로 진행

        Page<Feeds> feedsPage = feedsRepository.findByUser_UserId(userId, pageable);  // userId에 맞는 Feed들 Page로 받음
        List<Long> feedIdsList = feedsPage.stream().map(Feeds::getFeedId).toList(); // Feed내에서 Id값만 리스트로 정리

        //null 값 때문에 Object로 받고 Map으로 key: feedId, value: count한 like값
        Map<Long, Integer> likeTotalMap =likeRepository.countLikedByFeedIds(feedIdsList).stream()
                                        .collect(Collectors.toMap(row ->(Long) row[0], row ->((Long) row[1]).intValue()));

        //null 값 때문에 Object로 받고 Map으로 Key: feedId, value: count한 comments값
        Map<Long, Integer> commentsTotal = commentsRepository.countCommentsByFeedIds(feedIdsList).stream()
                                            .collect(Collectors.toMap(row ->(Long) row[0], row ->((Long) row[1]).intValue()));


        // null 값 때문에 Object로 받고 Map으로 Key: feedId, value: liked true값
        Map<Long, Boolean> liked = likeRepository.isLikedByFeedIdsANDUserId(feedIdsList, userDetails.getUserId()).stream()
                                        .collect(Collectors.toMap(row->(Long) row[0], row ->(Boolean) row[1]));


        Map<Long, List<String>> feedImages = feedImgRepository.findFeedImgByFeedId(feedIdsList).stream()
                        .collect(Collectors.groupingBy(row -> (Long) row[0], // feedId를 기준으로 그룹화
                                 Collectors.mapping(row -> (String) row[1], // feedImageUrl만 뽑아서
                                 Collectors.toList() // 리스트로 묶음
                        )));

        List<ReadUsersFeedsResponseDto> feedResponseDtoList = feedsPage.stream()
                .map(feed -> new ReadUsersFeedsResponseDto(
                        feed.getFeedId(),                                               // FeedId 값
                        feedImages.get(feed.getFeedId()),                                 // FeedImageUrl List값
                        feed.getContents(),                                             // Contents 값
                        likeTotalMap.getOrDefault(feed.getFeedId(),0),           // likeTotal 값, 기본 0
                        commentsTotal.getOrDefault(feed.getFeedId(), 0),      // commentsTotal 값, 기본 0
                        liked.getOrDefault(feed.getFeedId(), false)         // liked 값, 기본 false
                        ))
                .toList();

        return new PageImpl<>(feedResponseDtoList, pageable, feedsPage.getTotalElements());
    }


    @Transactional
    public Page<SearchUserResponseDto> searchUser(String keyword, UserDetailsImpl userDetails, Pageable pageable){
        Page<Users> resultUserList = usersRepository.findByUserNameContaining(keyword, pageable);

        List<Long> resultUserIdList = resultUserList.stream()
                .map(Users::getUserId)
                .collect(Collectors.toList());

        Map<Long, Boolean> resultUserFollow = followsRepository.isFollowedByMyIdANDUserIds(userDetails.getUserId(), resultUserIdList).stream()
                .collect(Collectors.toMap(row->(Long) row[0], row ->(Boolean) row[1]));

        Page<SearchUserResponseDto> resultUser = resultUserList.map(
                user-> new SearchUserResponseDto(
                        user.getUserName(),
                        user.getProfileImageUrl(),
                        resultUserFollow.getOrDefault(user.getUserId(), false)
                ));
        return resultUser;
    }
}
