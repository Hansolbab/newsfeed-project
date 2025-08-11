package com.example.newsfeedproject.feeds.service;

import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.dto.CreateFeedRequestDto;
import com.example.newsfeedproject.feeds.dto.FeedResponseDto;
import com.example.newsfeedproject.feeds.dto.UpdateFeedRequestDto;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedsService {

    private final FeedsRepository feedsRepository;
    private final UsersRepository usersRepository;

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
                .build();

        // (이미지 관련 로직은 FeedImg 클래스가 구현된 후 추가될 예정)

        // 3. Feeds 엔티티 저장
        feedsRepository.save(feeds);

        // 생성된 Feeds 엔티티 자체를 반환 (Controller에서 DTO 변환)
        return feeds;
    }

    // 게시글 전체 조회 (페이징, 최신순)
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public Page<FeedResponseDto> getAllFeeds(int page, int size, String currentUserEmail) { // currentUserEmail은 더 이상 isLiked 판단에 사용되지 않음
        // 정렬 기준: createdAt 필드를 기준으로 내림차순 (최신순)
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        // 모든 게시글을 페이징하여 조회
        Page<Feeds> feedsPage = feedsRepository.findAll(pageable);

        return feedsPage.map(feeds -> new FeedResponseDto(feeds)); // 또는 FeedResponseDto::new
    }

    // 게시글 단건 조회
    @Transactional(readOnly = true)
    public FeedResponseDto getFeedById(Long feedId, String currentUserEmail) { // currentUserEmail은 더 이상 isLiked 판단에 사용되지 않음
        // 게시글 ID로 Feeds 엔티티 조회
        Feeds feeds = feedsRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다.")); // Custom Exception으로 변경 권장

        return new FeedResponseDto(feeds);
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

        // (이미지 관련 로직은 FeedImg 클래스가 구현된 후 추가될 예정)

        // 5. 업데이트된 Feeds 엔티티를 기반으로 응답 DTO 반환
        return new FeedResponseDto(feeds);
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
        feedsRepository.delete(feeds);
    }
}