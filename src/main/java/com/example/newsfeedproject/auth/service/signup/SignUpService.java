package com.example.newsfeedproject.auth.service.signup;

import com.example.newsfeedproject.auth.dto.signup.SignUpRequestDto;
import com.example.newsfeedproject.common.exception.auth.AuthErrorException;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import static com.example.newsfeedproject.common.exception.auth.AuthErrorCode.*;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Long signUp(SignUpRequestDto signUpRequestDto){
        if(usersRepository.existsByUserName(signUpRequestDto.getUserName())){
            throw new AuthErrorException(USER_ALREADY_EXISTS);
        }

        if(usersRepository.findByEmail(signUpRequestDto.getEmail()).isPresent()){
            throw new AuthErrorException(EMAIL_ALREADY_EXISTS);
        }

        String encode= passwordEncoder.encode(signUpRequestDto.getPassword());//비밀번호 암호화

        //엔티티 생성
        Users user=new Users(
                signUpRequestDto.getUserName(),
                signUpRequestDto.getPhoneNumber(),
                signUpRequestDto.getEmail(),
                encode // password
        ); //기본프로필

        Users saved=usersRepository.save(user);//유저저장
        return saved.getUserId();

    }
}
