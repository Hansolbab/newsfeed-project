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
    public void update(Long meId, String newPhoneNumber) {
        Users user = usersRepository.findById(meId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 본인 번호와 동일하면 막기
        if (newPhoneNumber.equals(user.getPhoneNumber())) {
            //409 Conflict
            throw new ResponseStatusException(HttpStatus.CONFLICT, "현재 전화번호와 동일합니다.");
        }

        // 변경
        user.setPhoneNumber(newPhoneNumber);
    }

}
