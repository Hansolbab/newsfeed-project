package com.example.newsfeedproject.feeds.service;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.common.exception.feeds.FeedsErrorException;
import static com.example.newsfeedproject.common.exception.users.UsersErrorCode.*;
import com.example.newsfeedproject.common.exception.users.UsersErrorException;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.dto.CreateFeedsRequestDto;
import com.example.newsfeedproject.feeds.dto.FeedsResponseDto;
import com.example.newsfeedproject.feeds.dto.UpdateFeedsRequestDto;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.follow.repository.FollowsRepository;
import com.example.newsfeedproject.likes.repository.LikesRepository;
import com.example.newsfeedproject.users.entity.AccessAble;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import com.example.newsfeedproject.feedimg.entity.FeedImage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.example.newsfeedproject.common.exception.feeds.FeedsErrorCode.*;

// 게시글 관련 비즈니스 로직 처리 서비스
@Service
@RequiredArgsConstructor
public class FeedsService {
    private final FeedsRepository feedsRepository;
    private final UsersRepository usersRepository;
    private final LikesRepository likesRepository;
    private final FollowsRepository followsRepository;

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

        // 2. 공개 범위에 따라 접근 허용
        AccessAble accessAble = feed.getAccessAble();

        switch (accessAble) {
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
    public Page<FeedsResponseDto> readAllFeeds(int page, int size, UserDetailsImpl userDetails) {


        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        String currentUserEmail = userDetails.getUsername(); // 이메일 값 들고옴

        // 현재 사용자 ID 조회 (liked 판단용)
        Long currentUserId = usersRepository.findByEmail(currentUserEmail)
                .map(Users::getUserId)
                .orElse(null); // 사용자를 찾을 수 없다면 null (비로그인 사용자 혹은 오류 상황)

        Page<Feeds> feedsPage;

        feedsPage = feedsRepository.findAccessibleFeeds(currentUserId, pageable);

        // Feeds 엔티티를 FeedResponseDto로 변환
        return feedsPage.map(feeds -> {
            boolean liked = (currentUserId != null) && likesRepository.findByUserIdAndFeedIdAndLikedTrue(currentUserId, feeds.getFeedId()).isPresent();


            // likeTotal, commentTotal은 Feeds 엔티티에 없으므로 DTO에서 0으로 초기화될 것
            return new FeedsResponseDto(feeds, liked);
        });
    }

    // 게시글 단건 조회
    @Transactional(readOnly = true)
    public FeedsResponseDto readFeedById(Long feedId,  UserDetailsImpl userDetails) {
        // deleted=false 조건 추가
        Feeds feeds = feedsRepository.findByFeedIdAndDeletedFalse(feedId)
                .orElseThrow(() -> new FeedsErrorException(POST_NOT_FOUND));

        if (!hasAccess(feeds, userDetails)) {
            throw new IllegalArgumentException("이 게시글에 접근할 권한이 없습니다.");
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