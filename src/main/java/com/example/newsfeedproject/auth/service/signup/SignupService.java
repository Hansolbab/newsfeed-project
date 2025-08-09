package com.example.newsfeedproject.auth.service.signup;


import com.example.newsfeedproject.auth.dto.signup.SignupRequestDto;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SignupService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Long signup(SignupRequestDto  dto){
        if(usersRepository.existsByUserName(dto.getUserName())){
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if(usersRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        //비밀번호 암호화
        String encode= passwordEncoder.encode(dto.getPassword());
        //엔티티 생성
        Users user=new Users(
                dto.getUserName(),
                dto.getPhoneNumber(),
                dto.getEmail(),
                encode // password
                 ); //기본프로필
        //유저저장
        Users saved=usersRepository.save(user);
        return saved.getUserId();

    }
}
