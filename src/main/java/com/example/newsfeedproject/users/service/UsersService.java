package com.example.newsfeedproject.users.service;

import com.example.newsfeedproject.common.dto.PrincipalRequestDto;
import com.example.newsfeedproject.common.dto.ReadUserSimpleResponseDto;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {

    @Autowired
    private final UsersRepository usersRepository;


    public ReadUserSimpleResponseDto readUserSimple(Long userId, PrincipalRequestDto principalRequestDto) {
        // userId(프로필 보려는 대상 userId) null 확인
        Optional<Users> user = usersRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("없는 유저입니다.");
        }
        // 본인 프로필 확인할때
//        if (principalRequestDto.getUserId() == userId) {
//          현재는 반환하는 값이 다른게 없어서 그대로 진행
//        }

        Users userSimple = user.get();
        return new ReadUserSimpleResponseDto(userSimple.getUserName(),userSimple.getProfileImg());
    }
}
