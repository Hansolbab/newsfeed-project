package com.example.newsfeedproject.comment.entity;

import com.example.newsfeedproject.feeds.entity.Feeds;
import com.example.newsfeedproject.users.entity.Users;
import jakarta.persistence.*;

@Entity
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name="contents")
    private String contents;

    // User 랑 mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId")
    private Users userComments;
    // Feed 랑 mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="feedId")
    private Feeds feedComments;


    @Column(name="deleted")
    private boolean deleted;
}
