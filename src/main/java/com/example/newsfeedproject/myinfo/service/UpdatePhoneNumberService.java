package com.example.newsfeedproject.myinfo.service;

import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UpdatePhoneNumberService {
    private final UsersRepository usersRepository;

    @Transactional
    public void update(Long me, String phoneNumber) {

        if(usersRepository.existsByPhoneNumber(phoneNumber)) {
            //409 Conflict
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 휴대폰 번호입니다.");
        }

        Users user = usersRepository.findById(me)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        user.setPhoneNumber(phoneNumber);
    }
}
