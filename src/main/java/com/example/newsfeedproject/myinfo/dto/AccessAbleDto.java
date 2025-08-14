package com.example.newsfeedproject.myinfo.dto;

import com.example.newsfeedproject.users.entity.AccessAble;
import lombok.Getter;

@Getter
public class AccessAbleDto {
    private AccessAble accessAble;
    public AccessAble getAccessAble() {
        return accessAble;
    }
}
