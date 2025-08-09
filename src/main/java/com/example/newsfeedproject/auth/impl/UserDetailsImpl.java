package com.example.newsfeedproject.auth.impl;


import com.example.newsfeedproject.users.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
// 인증객체로 사용될 유저 구현체
public class  UserDetailsImpl implements  UserDetails {

    private final Users user;

    public Users getUser(){
        return user;
    }

    //  권한 안 하기로 하더라도 만들긴 해야 됨
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); //빈 리스트로
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // 이메일을 로그인 식별자로 쓴다면
        return user.getEmail();
    }
    public Long getUserId() {
        return  user.getUserId();
    }

    public String getEmail() {
        return user.getEmail();
    }

   @Override public boolean isAccountNonExpired() {return true;}
    @Override public boolean isAccountNonLocked() {return true;}
    @Override public boolean isCredentialsNonExpired() {return true;}
    @Override public boolean isEnabled() {return true;}
}
