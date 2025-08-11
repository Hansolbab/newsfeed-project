package com.example.newsfeedproject.myinfo.service;

import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WithdrawAccountService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void withdraw(Long meId, String oldPassword) {
        Users user=usersRepository.findById(meId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                "사용자를 찾을 수 없습니다."));
        if(Boolean.TRUE.equals(user.getDeleted())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"이미 탈퇴한 사용자입니다.");
        }
        if(!passwordEncoder.matches(oldPassword,user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"비밀번호가 일치하지 않습니다.");
        }

        String scrambled=passwordEncoder.encode("DELETE:"+ UUID.randomUUID());

        user.softDelete(scrambled, LocalDateTime.now());
    }
}
