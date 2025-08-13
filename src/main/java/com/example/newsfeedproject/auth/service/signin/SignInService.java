package com.example.newsfeedproject.auth.service.signin;

import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.auth.dto.signin.SignInRequestDto;
import com.example.newsfeedproject.auth.dto.signin.SignInResponseDto;
import com.example.newsfeedproject.common.util.JwtUtil;
import com.example.newsfeedproject.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignInResponseDto signIn(SignInRequestDto signInRequestDto) {
        Users user = usersRepository.findByEmail(signInRequestDto.getEmail())
                .orElseThrow(()->new IllegalArgumentException("가입된 사용자가 아닙니다."));

        if(!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        //util 호출하여 JWT 토큰 생성
        String accessToken  = JwtUtil.createAccessToken(user.getUserId(), user.getEmail());

        String refreshToken = JwtUtil.createRefreshToken(user.getUserId());

        return new SignInResponseDto(accessToken, refreshToken);
    }
}
