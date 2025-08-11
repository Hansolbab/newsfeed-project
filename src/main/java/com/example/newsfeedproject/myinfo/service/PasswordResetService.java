package com.example.newsfeedproject.myinfo.service;

import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Transactional
    public void resetPassword(UserDetailsImpl me, String oldPassword, String newPassword) {
        Users user = usersRepository.findById(me.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다.");
        }

        System.out.println("[DBG] oldMatches=" + bCryptPasswordEncoder.matches(oldPassword, user.getPassword()));
        System.out.println("[DBG] newMatches=" + bCryptPasswordEncoder.matches(newPassword, user.getPassword()));

        if (bCryptPasswordEncoder.matches(newPassword, user.getPassword())) {
            System.out.println("[SERVICE] 같은 비번 감지됨 — 400 던짐");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "새 비밀번호가 기존 비밀번호와 같습니다.");

        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
    }

}
