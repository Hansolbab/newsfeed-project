package com.example.newsfeedproject.users.entity;

import lombok.Getter;

@Getter
public enum AccessAble {

    ALL_ACCESS("전체 공개"),
    FOLLOWER_ACCESS("팔로워에게만 공개"),
    NONE_ACCESS("나만 보기");

    private final String accessAble;

    AccessAble(String accessAble){
        this.accessAble =accessAble;
    }
}
