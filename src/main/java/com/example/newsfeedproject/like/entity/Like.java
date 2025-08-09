package com.example.newsfeedproject.like.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long likeId;

    @Column(name="userId")
    private Long userId;

    @Column(name="feedId")
    private Long feedId;

    @Column(name="isLiked")
    private boolean isLiked;

}
