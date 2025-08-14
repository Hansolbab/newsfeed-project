package com.example.newsfeedproject.myinfo.service;

import com.example.newsfeedproject.common.exception.auth.AuthErrorException;
import com.example.newsfeedproject.common.exception.users.UsersErrorException;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.example.newsfeedproject.common.exception.auth.AuthErrorCode.*;
import static com.example.newsfeedproject.common.exception.users.UsersErrorCode.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WithdrawAccountService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void withdraw(Long meId, String oldPassword) {
        //400
        Users user=usersRepository.findById(meId).orElseThrow(()->new AuthErrorException(USER_NOT_FOUND));
        //409
        if(Boolean.TRUE.equals(user.getDeleted())){
            throw new UsersErrorException(NO_SUCH_USER);
        }
        //401
        if(!passwordEncoder.matches(oldPassword,user.getPassword())){
            throw new AuthErrorException(CURRENT_PASSWORD_NOT_MATCH);
        }

        String scrambled=passwordEncoder.encode("DELETE:"+ UUID.randomUUID());

        user.softDelete(scrambled);
    }
}
