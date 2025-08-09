package com.example.newsfeedproject.feeds.service;

import com.example.newsfeedproject.feeds.entity.Feeds; // Feeds 엔티티 임포트
import com.example.newsfeedproject.feeds.dto.FeedCreateRequestDto;
import com.example.newsfeedproject.feeds.dto.FeedCreateResponseDto;
import com.example.newsfeedproject.feeds.dto.FeedResponseDto;
import com.example.newsfeedproject.feeds.dto.FeedUpdateRequestDto;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.like.repository.LikeRepository; // LikeRepository 임포트
import com.example.newsfeedproject.users.entity.Users; // Users 엔티티 임포트
import com.example.newsfeedproject.users.repository.UsersRepository; // UsersRepository 임포트
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
    private final LikeRepository likeRepository;

    // 게시글 생성 기능
    @Transactional
    public Feeds createFeed(FeedCreateRequestDto requestDto, String userEmail) { // userEmail 기반
        // 1. 게시글 작성 사용자 조회
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다.")); // Custom Exception으로 변경 권장

        // 2. Feeds 엔티티 생성
        Feeds feeds = Feeds.builder()
                .user(user)
                .contents(requestDto.getContents())
                .category(requestDto.getCategory())
                .build();

        // (이미지 관련 로직은 FeedImg 클래스가 구현된 후 추가될 예정)

        // 3. Feeds 엔티티 저장
        feedsRepository.save(feeds);

        // 생성된 Feeds 엔티티 자체를 반환 (Controller에서 DTO 변환)
        return feeds;
    }

    // 게시글 전체 조회 (페이징, 최신순)
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public Page<FeedResponseDto> getAllFeeds(int page, int size, String currentUserEmail) {
        // 정렬 기준: createdAt 필드를 기준으로 내림차순 (최신순)
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        // 모든 게시글을 페이징하여 조회
        Page<Feeds> feedsPage = feedsRepository.findAll(pageable);

        // 현재 사용자 ID 조회 (isLiked 판단용)
        Long currentUserId = usersRepository.findByEmail(currentUserEmail)
                .map(Users::getUserId)
                .orElse(null); // 사용자를 찾을 수 없다면 null (비로그인 사용자 혹은 오류 상황)

        // 조회된 Feeds 엔티티들을 FeedResponseDto로 변환
        return feedsPage.map(feeds -> {
            boolean isLiked = (currentUserId != null) && likeRepository.findByUserIdAndFeedIdAndLikedTrue(currentUserId, feeds.getFeedId()).isPresent();
            return new FeedResponseDto(feeds, isLiked);
        });
    }

    // 게시글 단건 조회
    @Transactional(readOnly = true)
    public FeedResponseDto getFeedById(Long feedId, String currentUserEmail) {
        // 게시글 ID로 Feeds 엔티티 조회
        Feeds feeds = feedsRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다.")); // Custom Exception으로 변경 권장

        // 현재 사용자 ID 조회 (isLiked 판단용)
        Long currentUserId = usersRepository.findByEmail(currentUserEmail)
                .map(Users::getUserId)
                .orElse(null);

        // 현재 사용자가 해당 게시글에 좋아요를 눌렀는지 확인
        boolean isLiked = (currentUserId != null) && likeRepository.findByUserIdAndFeedIdAndLikedTrue(currentUserId, feeds.getFeedId()).isPresent();

        // 조회된 Feeds 엔티티를 FeedResponseDto로 변환하여 반환
        return new FeedResponseDto(feeds, isLiked);
    }

    // 게시글 수정
    @Transactional
    public FeedResponseDto updateFeed(Long feedId, FeedUpdateRequestDto requestDto, String currentUserEmail) {
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

        // (이미지 관련 로직은 FeedImg 클래스가 구현된 후 추가될 예정)

        // 5. 변경된 Feeds 엔티티 저장 (트랜잭션 종료 시 더티 체킹에 의해 자동 반영됨)
        // feedsRepository.save(feeds); // save()를 명시적으로 호출해도 됨.

        // 6. 업데이트된 Feeds 엔티티를 기반으로 응답 DTO 반환
        boolean isLiked = likeRepository.findByUserIdAndFeedIdAndLikedTrue(currentUser.getUserId(), feeds.getFeedId()).isPresent();
        return new FeedResponseDto(feeds, isLiked);
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

        // 4. 게시글 삭제 (DB에서 실제로 삭제)
        feedsRepository.delete(feeds);

        // (선택 사항: 소프트 삭제로 변경 시)
        // feeds.softDelete();
    }
}