package com.example.newsfeedproject.feeds.service;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.feeds.repository.FeedsRepository;
import com.example.newsfeedproject.likes.entity.Likes;
import com.example.newsfeedproject.likes.repository.LikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedLikeService {
    private final FeedsRepository feedsRepository;
    private final LikesRepository likesRepository;


    public boolean feedLike(Long feedId, UserDetailsImpl userDetails)
    {
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
