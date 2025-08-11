package com.example.newsfeedproject.feedImg.entity;

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
