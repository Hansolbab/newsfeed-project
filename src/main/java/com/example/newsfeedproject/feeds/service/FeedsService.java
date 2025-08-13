package com.example.newsfeedproject.feeds.service;

import com.example.newsfeedproject.category.entity.Category;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.dto.CreateFeedRequestDto;
import com.example.newsfeedproject.feeds.dto.FeedResponseDto;
import com.example.newsfeedproject.feeds.dto.UpdateFeedRequestDto;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.likes.repository.LikesRepository;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import com.example.newsfeedproject.feedimg.entity.FeedImg;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



// 게시글 관련 비즈니스 로직 처리 서비스
@Service
@RequiredArgsConstructor
public class FeedsService {

    private final FeedsRepository feedsRepository;
    private final UsersRepository usersRepository;
    private final LikesRepository likesRepository;

    // 게시글 생성 기능
    @Transactional
    public Feeds createFeed(CreateFeedRequestDto requestDto, String userEmail) { // userEmail 기반
        // 1. 게시글 작성 사용자 조회
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다.")); // Custom Exception으로 변경 권장

        // 2. Feeds 엔티티 생성
        Feeds feeds = Feeds.builder()
                .user(user)
                .contents(requestDto.getContents())
                .category(requestDto.getCategory())
                // likeTotal, commentTotal은 @Builder에 초기화되지 않음 (엔티티에 필드 없음)
                .build();

        // 3. 이미지 URL 목록을 FeedImg 엔티티로 변환하여 Feeds에 연결
        for (String imageUrl : requestDto.getFeedImageUrlList()) {
            FeedImg feedImg = FeedImg.builder()
                    .feedImageUrl(imageUrl)
                    .deleted(false)
                    // feed 필드는 addFeedImg에서 설정
                    .build();
            feeds.addFeedImg(feedImg); // Feeds 엔티티의 addFeedImg 편의 메소드 사용
        }

        // 4. Feeds 엔티티 저장
        feedsRepository.save(feeds);

        // 생성된 Feeds 엔티티 자체를 반환 (Controller에서 DTO 변환)
        return feeds;
    }

    // 게시글 전체 조회 (페이징, 최신순)
    @Transactional(readOnly = true)
    public Page<FeedResponseDto> getAllFeeds(int page, int size, String currentUserEmail, Category category) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Feeds> feedsPage;
        if (category != null) {
            // 카테고리 필터링이 있는 경우 - deleted=false 조건 추가
            feedsPage = feedsRepository.findByCategoryAndDeletedFalse(category, pageable);
        } else {
            // 카테고리 필터링이 없는 경우 - deleted=false 조건 추가
            feedsPage = feedsRepository.findByDeletedFalse(pageable);
        }

        // 현재 사용자 ID 조회 (liked 판단용)
        Long currentUserId = usersRepository.findByEmail(currentUserEmail)
                .map(Users::getUserId)
                .orElse(null); // 사용자를 찾을 수 없다면 null (비로그인 사용자 혹은 오류 상황)

        // Feeds 엔티티를 FeedResponseDto로 변환
        return feedsPage.map(feeds -> {
            boolean liked = (currentUserId != null) && likesRepository.findByUserIdAndFeedIdAndLikedTrue(currentUserId, feeds.getFeedId()).isPresent();
            // likeTotal, commentTotal은 Feeds 엔티티에 없으므로 DTO에서 0으로 초기화될 것
            return new FeedResponseDto(feeds, liked);
        });
    }

    // 게시글 단건 조회
    @Transactional(readOnly = true)
    public FeedResponseDto getFeedById(Long feedId, String currentUserEmail) {
        // deleted=false 조건 추가
        Feeds feeds = feedsRepository.findByFeedIdAndDeletedFalse(feedId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 현재 사용자 ID 조회 (liked 판단용)
        Long currentUserId = usersRepository.findByEmail(currentUserEmail)
                .map(Users::getUserId)
                .orElse(null);

        // 현재 사용자가 해당 게시글에 좋아요를 눌렀는지 확인
        boolean liked = (currentUserId != null) && likesRepository.findByUserIdAndFeedIdAndLikedTrue(currentUserId, feeds.getFeedId()).isPresent();

        return new FeedResponseDto(feeds, liked);
    }

    // 게시글 수정
    @Transactional
    public FeedResponseDto updateFeed(Long feedId, UpdateFeedRequestDto requestDto, String currentUserEmail) {
        // 1. 게시글 조회 (존재 여부 및 작성자 권한 확인)
        Feeds feeds = feedsRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다.")); // Custom Exception으로 변경 권장

        // 2. 현재 로그인한 사용자 정보 조회
        Users currentUser = usersRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("현재 사용자를 찾을 수 없습니다.")); // Custom Exception

        // 3. 현재 로그인한 사용자가 게시글 작성자인지 확인
        if (!feeds.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다."); // Custom Exception으로 변경 권장
        }

        // 4. 게시글 내용 및 카테고리 업데이트
        feeds.update(requestDto.getContents(), requestDto.getCategory());

        // 5. 이미지 목록 업데이트 (기존 이미지 삭제 후 새로 추가)
        feeds.getFeedImageList().clear(); // 기존 이미지 목록 삭제

        // 요청 DTO의 새 이미지 URL 목록으로 FeedImg 엔티티 재생성 및 연결
        for (String imageUrl : requestDto.getFeedImageUrlList()) {
            FeedImg newFeedImg = FeedImg.builder()
                    .feedImageUrl(imageUrl)
                    .deleted(false)
                    .build();
            feeds.addFeedImg(newFeedImg); // 새 이미지 추가 (Feeds 엔티티의 addFeedImg 메소드 사용)
        }

        // 6. 업데이트된 Feeds 엔티티를 기반으로 응답 DTO 반환
        boolean liked = likesRepository.findByUserIdAndFeedIdAndLikedTrue(currentUser.getUserId(), feeds.getFeedId()).isPresent();
        return new FeedResponseDto(feeds, liked);
    }

    // 게시글 삭제
    @Transactional
    public void deleteFeed(Long feedId, String currentUserEmail) {
        // 1. 게시글 조회 (존재 여부 및 작성자 권한 확인)
        Feeds feeds = feedsRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다.")); // Custom Exception으로 변경 권장

        // 2. 현재 로그인한 사용자 정보 조회
        Users currentUser = usersRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("현재 사용자를 찾을 수 없습니다.")); // Custom Exception

        // 3. 현재 로그인한 사용자가 게시글 작성자인지 확인
        if (!feeds.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new IllegalArgumentException("게시글 삭제 권한이 없습니다."); // Custom Exception으로 변경 권장
        }

        // 4. 게시글 삭제
        feeds.softDelete();


    }

    // 1. 모든 소프트 삭제된 게시글 조회
    @Transactional(readOnly = true)
    public Page<FeedResponseDto> getAllDeletedFeeds(int page, int size, String currentUserEmail) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Feeds> feedsPage = feedsRepository.findByDeletedTrue(pageable); // deleted=true인 게시글만 조회

        Long currentUserId = usersRepository.findByEmail(currentUserEmail)
                .map(Users::getUserId)
                .orElse(null);

        return feedsPage.map(feeds -> {
            boolean liked = (currentUserId != null) && likesRepository.findByUserIdAndFeedIdAndLikedTrue(currentUserId, feeds.getFeedId()).isPresent();
            return new FeedResponseDto(feeds, liked);
        });
    }

    // 2. 특정 소프트 삭제된 게시글 단건 조회
    @Transactional(readOnly = true)
    public FeedResponseDto getDeletedFeedById(Long feedId, String currentUserEmail) {
        Feeds feeds = feedsRepository.findByFeedIdAndDeletedTrue(feedId) // deleted=true인 게시글만 조회
                .orElseThrow(() -> new IllegalArgumentException("삭제된 게시글을 찾을 수 없습니다."));

        Long currentUserId = usersRepository.findByEmail(currentUserEmail)
                .map(Users::getUserId)
                .orElse(null);

        boolean liked = (currentUserId != null) && likesRepository.findByUserIdAndFeedIdAndLikedTrue(currentUserId, feeds.getFeedId()).isPresent();

        return new FeedResponseDto(feeds, liked);
    }

    // 소프트 삭제된 게시글 복구 (restore) 메소드
    @Transactional
    public FeedResponseDto restoreFeed(Long feedId, String userEmail) {
        Feeds feeds = feedsRepository.findByFeedIdAndDeletedTrue(feedId) // 삭제된 게시글만 조회
                .orElseThrow(() -> new IllegalArgumentException("복구할 게시글이 없거나 삭제된 상태가 아닙니다."));

        Users currentUser = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("현재 사용자를 찾을 수 없습니다."));

        if (!feeds.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new IllegalArgumentException("게시글 복구 권한이 없습니다."); // 삭제한 사용자만 복구 가능 등 정책 필요
        }
        feeds.setDeleted(false); // deleted 필드를 false로 변경하여 복구

        // 복구 후 다시 정상 게시글처럼 DTO 반환
        boolean liked = likesRepository.findByUserIdAndFeedIdAndLikedTrue(currentUser.getUserId(), feeds.getFeedId()).isPresent();
        return new FeedResponseDto(feeds, liked);
    }


}