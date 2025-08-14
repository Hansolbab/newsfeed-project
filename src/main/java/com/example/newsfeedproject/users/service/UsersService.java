package com.example.newsfeedproject.users.service;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.comment.repository.CommentsRepository;
import com.example.newsfeedproject.common.dto.ReadUserSimpleResponseDto;
import com.example.newsfeedproject.common.exception.feeds.FeedsErrorException;
import com.example.newsfeedproject.common.exception.users.UsersErrorException;
import com.example.newsfeedproject.feedimg.repository.FeedImgRepository;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.follow.repository.FollowsRepository;
import com.example.newsfeedproject.likes.repository.LikesRepository;
import com.example.newsfeedproject.common.dto.ReadUsersFeedsResponseDto;
import com.example.newsfeedproject.users.dto.LikesInfoDto;
import com.example.newsfeedproject.common.dto.ReadUserSimpleResponseDto;
import com.example.newsfeedproject.users.entity.AccessAble;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.example.newsfeedproject.common.exception.users.UsersErrorCode.*;
import static com.example.newsfeedproject.common.exception.feeds.FeedsErrorCode.*;

import java.util.List;
import java.util.Map;
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

    // 유저 단일 권한 확인 // 권한이 있을때 true
    public boolean hasUserAuthorized(UserDetailsImpl userDetails, String authorities){
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role->role.equals(authorities));
    }
    // 접근 가능 한지 판단 // visibility 상태일때 true
    public boolean isUserAccessible(Long userId, AccessAble visibility){
        return usersRepository.existsByUserIdAndVisibility(userId, visibility);
    }
    // 팔로우 여부 판단 // Follow 상태일때 true
    public boolean isUserFollowingTarget(Long userId, Long targetId){
        return followsRepository.existsByFollower_UserIdAndFollowee_UserIdAndFollowedTrue(userId, targetId);
    }
    // 같은 사람인지 확인 // 본인일때 true
    public boolean isSameUser(Long userId, Long targetId){return userId.equals(targetId);}
    // userId 값의 User를 읽을 수 있는 권한 있을 때 Users 형태로 반환
    public Users userById(Long userId){
        return usersRepository.findById(userId)
                .orElseThrow(() -> new UsersErrorException(NO_SUCH_USER));
    }

    // 프로필 (프로필 이미지, 이름, 팔로우 여부)
    public ReadUserSimpleResponseDto readUserSimple(Long userId, UserDetailsImpl userDetails) {
        // 권한확인 로그인한 사람이 유저인지
        if (!hasUserAuthorized(userDetails, "ROLE_USER")) {throw new FeedsErrorException(USER_NOT_FOUND_CURRENT);}
        // 보는 사람과 볼 사람이 같은지 여부 확인 // 비공개(나만보기) // 다른 사람 볼 때 그 사람이 비공개일때
        if (!isSameUser(userDetails.getUserId(), userId) && isUserAccessible(userId, AccessAble.NONE_ACCESS)){
            throw new UsersErrorException(USER_IS_PRIVATE);
        }
        // 팔로우 여부 확인
        boolean isFollowed = isUserFollowingTarget(userDetails.getUserId(), userId);
        // 2. 팔로워 공개  // 다른 사람 볼 때 그 사람이 팔로워 공개일때
        if (!isSameUser(userDetails.getUserId(), userId) && isUserAccessible(userId,AccessAble.FOLLOWER_ACCESS) && !isFollowed){
            throw new UsersErrorException(FOLLOW_REQUIRED);
        }
        // 볼 유저가 있는지 확인 후 반환
        Users userSimple = userById(userId);

        return new ReadUserSimpleResponseDto(userSimple.getUserName(),userSimple.getProfileImageUrl(), isFollowed);
    }

    public Page<ReadUsersFeedsResponseDto> readUserFeed(
            Long userId,
            UserDetailsImpl userDetails,
            Pageable pageable
    ){
        // 권한확인 로그인한 사람이 유저인지
        if (!hasUserAuthorized(userDetails, "ROLE_USER")) {throw new FeedsErrorException(USER_NOT_FOUND_CURRENT);}
        // 보는 사람과 볼 사람이 같은지 여부 확인 // 비공개(나만보기) // 다른 사람 볼 때 그 사람이 비공개일때
        if (!isSameUser(userDetails.getUserId(), userId) && isUserAccessible(userId, AccessAble.NONE_ACCESS)){
            throw new UsersErrorException(USER_IS_PRIVATE);
        }
        // 팔로우 여부 확인
        boolean isFollowed = isUserFollowingTarget(userDetails.getUserId(), userId);
        // 2. 팔로워 공개  // 다른 사람 볼 때 그 사람이 팔로워 공개일때
        if (!isSameUser(userDetails.getUserId(), userId) && isUserAccessible(userId,AccessAble.FOLLOWER_ACCESS) && !isFollowed){
            throw new UsersErrorException(FOLLOW_REQUIRED);
        }
        // 볼 유저가 있는지 확인 후 반환
        Users user = userById(userId);
        Page<Feeds> feedsPage;
        // 본인 페이지 볼 때
        if (isSameUser(userDetails.getUserId(), userId)){
            feedsPage = feedsRepository.findAcceessibleFeedsMyPage(userId, pageable);
        } else { // 다른사람 페이지 볼 때
            if (isUserAccessible(userId, AccessAble.ALL_ACCESS)){ // 전체공개 유저 // 전체 공개 게시글
                feedsPage = feedsRepository.findAllAccessFeedsUserPage(userId, pageable);
            } else {
                feedsPage = feedsRepository.findFollowerAccessFeedsUserPage(userId, pageable);  // userId에 맞는 Feed들 Page로 받음
            }
        }

        List<Long> feedIdsList = feedsPage.stream().map(Feeds::getFeedId).toList(); // FeedId만 리스트로 정리
        //object로 받고 Map key: feedId, value LikeInfoDto
        Map<Long, LikesInfoDto> likesInfoMap = likeRepository.countLikesAndIsLikedByFeedIds(feedIdsList, userId).stream()
                .collect(Collectors.toMap(row -> (Long) row[0],
                        row-> new LikesInfoDto(
                                ((Long) row[1]).intValue(),
                           ((Long) row[2]) > 0)));
        //null 값 때문에 Object로 받고 Map Key: feedId, value: count한 comments값
        Map<Long, Integer> commentsTotal = commentsRepository.countCommentsByFeedIds(feedIdsList).stream()
                                            .collect(Collectors.toMap(row ->(Long) row[0], row ->((Long) row[1]).intValue()));

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
                        likesInfoMap.getOrDefault(feed.getFeedId(),new LikesInfoDto(0, false)).getLikeTotal(),           // likeTotal 값, 기본 0
                        commentsTotal.getOrDefault(feed.getFeedId(), 0),      // commentsTotal 값, 기본 0
                        likesInfoMap.getOrDefault(feed.getFeedId(),new LikesInfoDto(0, false)).isLiked()         // liked 값, 기본 false
                        ))
                .toList();

        return new PageImpl<>(feedResponseDtoList, pageable, feedsPage.getTotalElements());
    }


    @Transactional
    public Page<ReadUserSimpleResponseDto> searchUser(String keyword, UserDetailsImpl userDetails, Pageable pageable){
        Page<Users> resultUserList = usersRepository.findByUserNameContainingAndDeletedFalseAndNOTNoneAccess(keyword, pageable);

        List<Long> resultUserIdList = resultUserList.stream()
                .map(Users::getUserId)
                .collect(Collectors.toList());

        Map<Long, Boolean> resultUserFollow = followsRepository.isFollowedByMyIdANDUserIds(userDetails.getUserId(), resultUserIdList).stream()
                .collect(Collectors.toMap(row->(Long) row[0], row ->(Boolean) row[1]));

        Page<ReadUserSimpleResponseDto> resultUser = resultUserList.map(
                user-> new ReadUserSimpleResponseDto(
                        user.getUserName(),
                        user.getProfileImageUrl(),
                        resultUserFollow.getOrDefault(user.getUserId(), false)
                ));
        return resultUser;
    }
}
