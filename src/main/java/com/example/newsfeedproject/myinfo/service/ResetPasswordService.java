package com.example.newsfeedproject.myinfo.service;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.common.exception.auth.AuthErrorException;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.example.newsfeedproject.common.exception.auth.AuthErrorCode.*;
@Service
@RequiredArgsConstructor
public class ResetPasswordService {
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void resetPassword(UserDetailsImpl userDetailsImpl, String oldPassword, String newPassword) {
        Users user = usersRepository.findById(userDetailsImpl.getUserId())
                .orElseThrow(() -> new AuthErrorException(USER_NOT_FOUND));

        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AuthErrorException(CURRENT_PASSWORD_NOT_MATCH);
        }

//        System.out.println("[DBG] oldMatches=" + bCryptPasswordEncoder.matches(oldPassword, user.getPassword()));
//        System.out.println("[DBG] newMatches=" + bCryptPasswordEncoder.matches(newPassword, user.getPassword()));

        if (bCryptPasswordEncoder.matches(newPassword, user.getPassword())) {
//            System.out.println("[SERVICE] 같은 비번 감지됨 — 400 던짐");
            throw new AuthErrorException(NEW_PASSWORD_SAME_AS_OLD);
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
    }

}
