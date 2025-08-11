package com.example.newsfeedproject.feedimg.entity;

import jakarta.persistence.*;

@Entity
public class FeedImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedImgId;

    @Column(name="feedImg")
    private String feedImg;

    @Column(name="deleted")
    private boolean deleted;

}
