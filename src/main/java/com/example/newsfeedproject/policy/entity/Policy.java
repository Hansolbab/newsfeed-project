package com.example.newsfeedproject.policy.entity;

import jakarta.persistence.*;

@Entity
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedImgId;

    @Column(name="feedImg")
    private String feedImg;

    @Column(name="deleted")
    private boolean deleted;

}
