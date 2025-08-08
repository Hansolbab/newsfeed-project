package com.example.newsfeedproject.users.service;

import com.example.newsfeedproject.common.dto.ReadUserSimpleResponseDto;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {

    @Autowired
    private final UsersRepository usersRepository;

    public ReadUserSimpleResponseDto readUserSimple(Long userId){
        // null 확인
        Optional<Users> user = usersRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("없는 유저입니다.");
        }
        Users userSimple = user.get();
        return new ReadUserSimpleResponseDto(userSimple.getUserName(),userSimple.getProfileImg());
    }
}
