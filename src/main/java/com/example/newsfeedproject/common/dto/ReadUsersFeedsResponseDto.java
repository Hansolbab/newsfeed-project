package com.example.newsfeedproject.common.dto;

import com.example.newsfeedproject.category.entity.Category;
import lombok.Getter;
import lombok.Locked;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReadUsersFeedsResponseDto {
    private Long feedId;
    private List<String> feedImageURL;
    private Category category;
    private String contents;
    private Integer likeTotal;
    private Integer commentTotal;
    private boolean liked;
    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
    private String userName;
    private String userProfileImageUrl;
    private boolean followed;

    public ReadUsersFeedsResponseDto(Long feedId, List<String> feedImgs, String contents, Integer likeTotal, Integer commentTotal, Boolean liked, Category category, String userName, String userProfileImageUrl, boolean followed, LocalDateTime createdAt) {
        this.feedId = feedId;
        this.feedImageURL = feedImgs;
        this.contents = contents;
        this.likeTotal = likeTotal;
        this.commentTotal = commentTotal;
        this.liked = liked;
        this.category = category;
        this.userName = userName;
        this.userProfileImageUrl = userProfileImageUrl;
        this.followed = followed;
        this.createdAt = createdAt;
    }

    public ReadUsersFeedsResponseDto(Long feedId, List<String> feeImgs, String contents, Integer likeTotal, Integer commentTotal, boolean liked){

        this.feedId = feedId;
        this.feedImageURL = feeImgs;
        this.contents = contents;
        this.likeTotal = likeTotal;
        this.commentTotal = commentTotal;
        this.liked = liked;


    }


}
