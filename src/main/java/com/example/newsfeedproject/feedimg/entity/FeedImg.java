package com.example.newsfeedproject.feedimg.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class FeedImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedId;

}
