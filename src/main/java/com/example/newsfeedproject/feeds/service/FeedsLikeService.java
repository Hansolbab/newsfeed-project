package com.example.newsfeedproject.feeds.service;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.likes.entity.Likes;
import com.example.newsfeedproject.likes.repository.LikesRepository;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedsLikeService {
    private final FeedsRepository feedsRepository;
    private final LikesRepository likesRepository;
    private final UsersRepository usersRepository;

    public boolean feedLike(Long feedId, UserDetailsImpl userDetails
    ){
        String userEmail = userDetails.getUsername(); // 이메일 값 들고옴
        // 1. 게시글 작성 사용자 조회
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다.")); // Custom Exception으로 변경 권장

        // 로그인 여부
        if (userDetails == null) {throw new IllegalArgumentException("로그인 필요");}
        // Feed 존재 여부
        if (!feedsRepository.existsById(feedId)){throw new IllegalArgumentException("피드 없음");}

        // user가 feed like할 권한이 있는지 확인 // 일단 나중에 추가

        // user가 feed like 여부 확인
        Optional<Likes> existFeedLike = likesRepository.findByUserIdAndFeedId(userDetails.getUserId(), feedId);
        if(existFeedLike.isPresent()){
            Likes feedLike =  existFeedLike.get();
            feedLike.setLiked(!existFeedLike.get().isLiked());
            likesRepository.save(feedLike);

            return feedLike.isLiked();
        } else {
            Likes feedLike = new Likes(userDetails.getUserId(), feedId, true);
            likesRepository.save(feedLike);

            return feedLike.isLiked();
        }

    }

    public Long feedLikeCount(Long feedId){
        // 피드 존재 여부 확인
        if (!feedsRepository.existsById(feedId)){throw new IllegalArgumentException("피드 없음");}

        return likesRepository.countByFeedIdAndLikedTrue(feedId);
    }
}
