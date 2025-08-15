package com.example.newsfeedproject.myinfo.service;

import com.example.newsfeedproject.common.exception.auth.AuthErrorException;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.example.newsfeedproject.common.exception.auth.AuthErrorCode.*;
@Service
@RequiredArgsConstructor
public class UpdatePhoneNumberService {
    private final UsersRepository usersRepository;

    @Transactional
    public void update(Long meId, String newPhoneNumber) {
        Users user = usersRepository.findById(meId)
                .orElseThrow(() -> new AuthErrorException(USER_NOT_FOUND));

        // 본인 번호와 동일하면 막기
        if (newPhoneNumber.equals(user.getPhoneNumber())) {
            //409 Conflict
            throw new AuthErrorException(PHONE_NUMBER_SAME);
        }
        user.setPhoneNumber(newPhoneNumber);
    }

}
