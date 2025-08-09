package com.example.newsfeedproject.feeds.service;

import com.example.newsfeedproject.feeds.dto.*;
import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.users.entity.Users; // Users 엔티티 임포트
import com.example.newsfeedproject.users.repository.UsersRepository; // UsersRepository 임포트
import com.example.newsfeedproject.category.entity.Category; // Category enum 임포트
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedsService {

    private final FeedsRepository feedsRepository;
    private final UsersRepository usersRepository; // UsersRepository 주입

    // --- 게시글 생성 (CREATE) ---
    @Transactional
    public FeedResponseDto createFeed(FeedCreateRequestDto requestDto, String currentUserName) {
        // currentUserName (UserDetails.getUsername())이 Users의 userName과 매핑된다고 가정
        Users currentUser = usersRepository.findByEmail(currentUserName) // usersRepository 사용
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found for creation"));

        Category category;
        try {
            category = Category.valueOf(requestDto.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category name provided: " + requestDto.getCategory());
        }

        Feeds feeds = Feeds.builder()
                .user(currentUser)
                .contents(requestDto.getContents())
                .feedImgs(requestDto.getFeedImgs())
                .category(category)
                .commentTotal(0)
                .likeTotal(0)
                .build();

        Feeds savedFeeds = feedsRepository.save(feeds);

        return convertToFeedResponseDto(savedFeeds, false);
    }

    // --- 게시글 단건 조회 (READ One) ---
    public FeedResponseDto getFeedById(Long feedId, String currentUserName) {
        Feeds feeds = feedsRepository.findById(feedId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feeds not found with ID: " + feedId));

        boolean isLiked = checkIfUserLikedFeed(currentUserName, feedId);

        return convertToFeedResponseDto(feeds, isLiked);
    }

    // --- 전체 피드 조회 (READ All, 최신순, 페이지네이션) ---
    public List<FeedResponseDto> getAllFeeds(int page, int size, String currentUserName) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Feeds> feedsPage = feedsRepository.findAllByOrderByFeedIdDesc(pageable);

        return feedsPage.getContent().stream()
                .map(feeds -> convertToFeedResponseDto(feeds, checkIfUserLikedFeed(currentUserName, feeds.getFeedId())))
                .collect(Collectors.toList());
    }

    // --- 게시글 수정 (UPDATE) ---
    @Transactional
    public FeedResponseDto updateFeed(Long feedId, FeedUpdateRequestDto requestDto, String currentUserName) {
        Feeds feeds = feedsRepository.findById(feedId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feeds not found with ID: " + feedId));

        // Users 엔티티에는 getUserName() 메서드가 있으므로 그대로 사용 가능
        if (!feeds.getUser().getUserName().equals(currentUserName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update this feeds.");
        }

        Category updatedCategory;
        try {
            updatedCategory = Category.valueOf(requestDto.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category name provided: " + requestDto.getCategory());
        }

        feeds.setContents(requestDto.getContent());
        feeds.setFeedImgs(requestDto.getFeedImgs());
        feeds.setCategory(updatedCategory);

        Feeds updatedFeeds = feedsRepository.save(feeds);

        boolean isLiked = checkIfUserLikedFeed(currentUserName, feedId);
        return convertToFeedResponseDto(updatedFeeds, isLiked);
    }

    // --- 게시글 삭제 (DELETE) ---
    @Transactional
    public void deleteFeed(Long feedId, String currentUserName) {
        Feeds feeds = feedsRepository.findById(feedId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feeds not found with ID: " + feedId));

        // Users 엔티티에는 getUserName() 메서드가 있으므로 그대로 사용 가능
        if (!feeds.getUser().getUserName().equals(currentUserName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this feeds.");
        }

        feedsRepository.delete(feeds);
    }

    // ----- 보조 메서드 -----

    // Feeds 엔티티를 FeedResponseDto로 변환하는 헬퍼 메서드
    private FeedResponseDto convertToFeedResponseDto(Feeds feeds, boolean isLiked) {
        // Users 엔티티의 userName과 profileImg를 사용
        UserResponseDto userDto = new UserResponseDto(feeds.getUser().getUserName(), feeds.getUser().getProfileImg());

        String categoryDisplayName = feeds.getCategory().toString();

        return new FeedResponseDto(
                feeds.getFeedId(),
                userDto,
                feeds.getContents(),
                feeds.getFeedImgs(),
                categoryDisplayName,
                feeds.getLikeTotal(),
                feeds.getCommentTotal(),
                isLiked
        );
    }

    // 특정 유저가 특정 피드에 좋아요를 눌렀는지 확인하는 (가상) 로직
    private boolean checkIfUserLikedFeed(String userName, Long feedId) {
        return "testUser".equals(userName) && feedId == 1L;
    }
}