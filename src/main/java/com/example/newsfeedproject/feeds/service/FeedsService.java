package com.example.newsfeedproject.feeds.service;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.category.entity.Category;
import com.example.newsfeedproject.comment.repository.CommentsRepository;
import com.example.newsfeedproject.common.dto.ReadUserSimpleResponseDto;
import com.example.newsfeedproject.common.dto.ReadUsersFeedsResponseDto;
import com.example.newsfeedproject.common.exception.auth.AuthErrorException;
import com.example.newsfeedproject.common.exception.feeds.FeedsErrorException;

import static com.example.newsfeedproject.common.exception.auth.AuthErrorCode.USER_NOT_FOUND;
import static com.example.newsfeedproject.common.exception.users.UsersErrorCode.*;
import com.example.newsfeedproject.common.exception.users.UsersErrorException;
import com.example.newsfeedproject.feedimg.repository.FeedImgRepository;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.dto.CreateFeedsRequestDto;
import com.example.newsfeedproject.feeds.dto.FeedsResponseDto;
import com.example.newsfeedproject.feeds.dto.UpdateFeedsRequestDto;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.follow.repository.FollowsRepository;
import com.example.newsfeedproject.likes.repository.LikesRepository;
import com.example.newsfeedproject.users.dto.LikesInfoDto;
import com.example.newsfeedproject.users.entity.AccessAble;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import com.example.newsfeedproject.feedimg.entity.FeedImage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.newsfeedproject.common.exception.feeds.FeedsErrorCode.*;

// 게시글 관련 비즈니스 로직 처리 서비스
@Service
@RequiredArgsConstructor
public class FeedsService {
    private final FeedsRepository feedsRepository;
    private final UsersRepository usersRepository;
    private final LikesRepository likesRepository;
    private final CommentsRepository commentsRepository;
    private final FollowsRepository followsRepository;
    private final FeedImgRepository feedImgRepository;

    // 게시글 생성 기능
    @Transactional
    public Feeds createFeed(CreateFeedsRequestDto createFeedsRequestDto, UserDetailsImpl userDetails) { // userEmail 기반

        String userEmail = userDetails.getUsername(); // 이메일 값 들고옴
        // 1. 게시글 작성 사용자 조회
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new FeedsErrorException(USER_NOT_FOUND_CURRENT)); // Custom Exception으로 변경 권장

        // 2. Feeds 엔티티 생성
        Feeds feeds = Feeds.builder()
                .user(user)
                .contents(createFeedsRequestDto.getContents())
                .category(createFeedsRequestDto.getCategory())
                .accessAble(createFeedsRequestDto.getAccessAble())
                .build();

        // 3. 이미지 URL 목록을 FeedImg 엔티티로 변환하여 Feeds에 연결
        for (String imageUrl : createFeedsRequestDto.getFeedImageUrlList()) {
            FeedImage feedImg = FeedImage.builder()
                    .feedImageUrl(imageUrl)
                    .feed(feeds)
                    .deleted(false)
                    .build();
            feeds.getFeedImageList().add(feedImg);
        }
        // 4. Feeds 엔티티 저장
        feedsRepository.save(feeds);
        // 생성된 Feeds 엔티티 자체를 반환 (Controller에서 DTO 변환)
        return feeds;
    }

    // 게시글 접근 권한 확인 메소드
    private boolean hasAccess(Feeds feed, UserDetailsImpl userDetails) {
        Long currentUserId = userDetails.getUserId();
        Long feedOwnerId = feed.getUser().getUserId();

        // 1. 게시글 주인이면 언제든 접근 가능
        if (currentUserId.equals(feedOwnerId)) {
            return true;
        }

        Users feedOwner = feed.getUser();
        AccessAble ownerProfileVisibility = feedOwner.getVisibility();

        switch (ownerProfileVisibility) {
            case ALL_ACCESS:
                break;
            case FOLLOWER_ACCESS:
                if (!followsRepository.existsByFollower_UserIdAndFollowee_UserIdAndFollowedTrue(currentUserId, feedOwnerId)) {
                    return false;
                }
                break;
            case NONE_ACCESS:
            default:
                return false;
        }
//  유저는 전체공개, 게시글은 팔로우 공개에요   팔로우가 아닌 사람이 게시글을 볼 때 전체 공개인 게시글들은 보일거에요 팔로우 전용 게시글은 안보일거에요
        AccessAble feedAccessAble = feed.getAccessAble();

        switch (feedAccessAble) {
            case ALL_ACCESS:
                return true;
            case FOLLOWER_ACCESS:
                return followsRepository.existsByFollower_UserIdAndFollowee_UserIdAndFollowedTrue(currentUserId, feedOwnerId);
            case NONE_ACCESS:
            default:
                return false;
        }
    }

    // 게시글 전체 조회 (페이징, 최신순)
    @Transactional(readOnly = true)
    public Page<ReadUsersFeedsResponseDto> readAllFeeds(int page, int size, UserDetailsImpl userDetails) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        size = 3;
        Pageable pageable = PageRequest.of(page, size, sort);

        Users user = usersRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new AuthErrorException(USER_NOT_FOUND));

        Long meId = user.getUserId();

        // 권한별로 나눠서 Feed 조회 후 페이징
//        Page<Feeds> feedsPage = feedsRepository.findAccessibleFeedsBasedOnProfile(meId, pageable);
        Page<Feeds> feedsPage = feedsRepository.findAllFeedConditional(meId, Category.KOREAN,pageable);

        List<Long> feedIdList = feedsPage.getContent().stream()
                .map(Feeds::getFeedId)
                .toList();

        Map<Long, List<String>> feedImagesMap = feedImgRepository.findFeedImgByFeedId(feedIdList).stream()
                .collect(Collectors.groupingBy(row -> (Long) row[0], // feedId를 기준으로 그룹화
                        Collectors.mapping(row -> (String) row[1], // feedImageUrl만 뽑아서
                                Collectors.toList() // 리스트로 묶음
                        )));

        Map<Long, Integer> likeTaotalMap = likesRepository.countLikedByFeedIds(feedIdList).stream()
                .collect(Collectors.toMap(row -> (Long) row[0],
                        row-> ((Long) row[1]).intValue()));

        // 개선사항으로 했지만 오류 발견후 추후 수정
//        Map<Long, LikesInfoDto> likesInfoMap = likesRepository.countLikesAndIsLikedByFeedIds(feedIdList, meId).stream()
//                .collect(Collectors.toMap(
//                        row -> (Long) row[0],
//                        row -> new LikesInfoDto(
//                                 ((Long) row[2]).intValue(),
//                            ((Long) row[1]) > 0
//                        )));



        //댓글 토탈
        Map<Long, Integer> commentsTotalMap = commentsRepository.countCommentsByFeedIds(feedIdList).stream()
                .collect(Collectors.toMap(row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()));

        Map<Long, Boolean> likedMap = likesRepository.isLikedByFeedIdsANDUserId(feedIdList, user.getUserId()).stream()
                .collect(Collectors.toMap(row -> (Long) row[0],
                        row ->(boolean) row[1]));

        Set<Long> followedUserIdSet = followsRepository.findFolloweeIdsByMe(meId);

        List<ReadUsersFeedsResponseDto> feedsResponseDtoList = feedsPage.stream()
                .map(feed -> new ReadUsersFeedsResponseDto(
                        feed.getFeedId(),
                        feedImagesMap.get(feed.getFeedId()),
                        feed.getContents(),
                        likeTaotalMap.getOrDefault(feed.getFeedId(),  0),
                        commentsTotalMap.getOrDefault(feed.getFeedId(), 0),
                        likedMap.getOrDefault(feed.getFeedId(),false),
                        feed.getCategory(),
                        feed.getUser().getUserName(),
                        feed.getUser().getProfileImageUrl(),
                        followedUserIdSet.contains(feed.getUser().getUserId()),
                        feed.getCreatedAt(),
                        feed.getUser().getUserId())).toList();

        return new PageImpl<>(feedsResponseDtoList, pageable, feedsPage.getTotalElements());
        }
//
//        this.feedId = feedId;
//        this.feedImageURL = feedImgs;
//        this.contents = contents;
//        this.likeTotal = likeTotal;
//        this.commentTotal = commentTotal;
//        this.liked = liked;

    // 게시글 단건 조회
    @Transactional(readOnly = true)
    public FeedsResponseDto readFeedById(Long feedId,  UserDetailsImpl userDetails) {
        // deleted=false 조건 추가
        Feeds feeds = feedsRepository.findByFeedIdAndDeletedFalse(feedId)
                .orElseThrow(() -> new FeedsErrorException(POST_NOT_FOUND));

        if (!hasAccess(feeds, userDetails)) {
            throw new FeedsErrorException(POST_IS_PRIVATE);
        }

        String currentUserEmail = userDetails.getUsername(); // 이메일 값 들고옴

        // 현재 사용자 ID 조회 (liked 판단용)
        Long currentUserId = usersRepository.findByEmail(currentUserEmail)
                .map(Users::getUserId)
                .orElse(null);

        // 현재 사용자가 해당 게시글에 좋아요를 눌렀는지 확인
        boolean liked = (currentUserId != null) && likesRepository.findByUserIdAndFeedIdAndLikedTrue(currentUserId, feeds.getFeedId()).isPresent();

        return new FeedsResponseDto(feeds, liked);
    }

    // 게시글 수정
    @Transactional
    public FeedsResponseDto updateFeed(Long feedId, UpdateFeedsRequestDto updateFeedsRequestDto,
                                       UserDetailsImpl userDetails) {

        String currentUserEmail = userDetails.getUsername(); // 이메일 값 들고옴

        // 1. 게시글 조회 (존재 여부 및 작성자 권한 확인)
        Feeds feeds = feedsRepository.findById(feedId)
                .orElseThrow(() -> new FeedsErrorException(POST_NOT_FOUND)); // Custom Exception으로 변경 권장

        // 2. 현재 로그인한 사용자 정보 조회
        Users currentUser = usersRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new FeedsErrorException(USER_NOT_FOUND_CURRENT)); // Custom Exception

        // 3. 현재 로그인한 사용자가 게시글 작성자인지 확인
        if (!feeds.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UsersErrorException(NOT_POST_AUTHOR); // Custom Exception으로 변경 권장
        }

        // 4. 게시글 내용 및 카테고리 업데이트
        feeds.update(updateFeedsRequestDto.getContents(), updateFeedsRequestDto.getCategory());

        // 5. 이미지 목록 업데이트 (기존 이미지 삭제 후 새로 추가)
        feeds.getFeedImageList().clear(); // 기존 이미지 목록 삭제

        // 요청 DTO의 새 이미지 URL 목록으로 FeedImg 엔티티 재생성 및 연결
        for (String imageUrl : updateFeedsRequestDto.getFeedImageUrlList()) {
            FeedImage newFeedImg = FeedImage.builder()
                    .feedImageUrl(imageUrl)
                    .feed(feeds)
                    .deleted(false)
                    .build();
            feeds.getFeedImageList().add(newFeedImg); // 새 이미지 추가 (Feeds 엔티티의 addFeedImg 메소드 사용)
        }

        // 6. 업데이트된 Feeds 엔티티를 기반으로 응답 DTO 반환
        boolean liked = likesRepository.findByUserIdAndFeedIdAndLikedTrue(currentUser.getUserId(), feeds.getFeedId()).isPresent();
        return new FeedsResponseDto(feeds, liked);
    }

    // 게시글 삭제
    @Transactional
    public void deleteFeed(Long feedId,  UserDetailsImpl userDetails) {

        String currentUserEmail = userDetails.getUsername(); // 이메일 값 들고옴

        // 1. 게시글 조회 (존재 여부 및 작성자 권한 확인)
        Feeds feeds = feedsRepository.findById(feedId)
                .orElseThrow(() -> new FeedsErrorException(POST_NOT_FOUND)); // Custom Exception으로 변경 권장

        // 2. 현재 로그인한 사용자 정보 조회
        Users currentUser = usersRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UsersErrorException(NOT_POST_AUTHOR)); // Custom Exception

        // 3. 현재 로그인한 사용자가 게시글 작성자인지 확인
        if (!feeds.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UsersErrorException(NOT_POST_AUTHOR); // Custom Exception으로 변경 권장
        }

        // 4. 게시글 삭제
        feeds.softDelete();


    }

    // 1. 모든 소프트 삭제된 게시글 조회
    @Transactional(readOnly = true)
    public Page<FeedsResponseDto> readAllDeletedFeeds(int page, int size, UserDetailsImpl userDetails) {


        String currentUserEmail = userDetails.getUsername(); // 이메일 값 들고옴

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Feeds> feedsPage = feedsRepository.findByDeletedTrue(pageable); // deleted=true인 게시글만 조회

        Long currentUserId = usersRepository.findByEmail(currentUserEmail)
                .map(Users::getUserId)
                .orElse(null);

        return feedsPage.map(feeds -> {
            boolean liked = (currentUserId != null) && likesRepository.findByUserIdAndFeedIdAndLikedTrue(currentUserId, feeds.getFeedId()).isPresent();
            return new FeedsResponseDto(feeds, liked);
        });
    }

    // 2. 특정 소프트 삭제된 게시글 단건 조회
    @Transactional(readOnly = true)
    public FeedsResponseDto readDeletedFeedById(Long feedId,  UserDetailsImpl userDetails) {


        String currentUserEmail = userDetails.getUsername(); // 이메일 값 들고옴

        Feeds feeds = feedsRepository.findByFeedIdAndDeletedTrue(feedId) // deleted=true인 게시글만 조회
                .orElseThrow(() -> new FeedsErrorException(POST_NOT_FOUND));

        Long currentUserId = usersRepository.findByEmail(currentUserEmail)
                .map(Users::getUserId)
                .orElse(null);

        boolean liked = (currentUserId != null) && likesRepository.findByUserIdAndFeedIdAndLikedTrue(currentUserId, feeds.getFeedId()).isPresent();

        return new FeedsResponseDto(feeds, liked);
    }

    // 소프트 삭제된 게시글 복구 (restore) 메소드
    @Transactional
    public FeedsResponseDto restoreFeed(Long feedId,  UserDetailsImpl userDetails) {


        String userEmail = userDetails.getUsername(); // 이메일 값 들고옴

        Feeds feeds = feedsRepository.findByFeedIdAndDeletedTrue(feedId) // 삭제된 게시글만 조회
                .orElseThrow(() -> new FeedsErrorException(NOT_FOUND_RESET_POST));

        Users currentUser = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsersErrorException(NOT_POST_AUTHOR));

        if (!feeds.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UsersErrorException(NOT_POST_AUTHOR); // 삭제한 사용자만 복구 가능 등 정책 필요
        }
        feeds.setDeleted(false); // deleted 필드를 false로 변경하여 복구

        // 복구 후 다시 정상 게시글처럼 DTO 반환
        boolean liked = likesRepository.findByUserIdAndFeedIdAndLikedTrue(currentUser.getUserId(), feeds.getFeedId()).isPresent();
        return new FeedsResponseDto(feeds, liked);
    }
}