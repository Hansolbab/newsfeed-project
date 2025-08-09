package com.example.newsfeedproject.auth.service.signin;

import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.auth.dto.signin.SigninRequestDto;
import com.example.newsfeedproject.auth.dto.signin.SigninResponseDto;
import com.example.newsfeedproject.common.util.JwtUtil;
import com.example.newsfeedproject.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// JWT토큰 기반
// 유저 찾아서 비밀번호 검사하고 토큰 2개 생성해서 응답
@Service
@RequiredArgsConstructor
public class SigninService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SigninResponseDto signin(SigninRequestDto dto) {
        Users user = usersRepository.findByEmail(dto.getEmail())
                .orElseThrow(()->new IllegalArgumentException("가입된 사용자가 아닙니다."));

        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        //util 호출하여 JWT 토큰 생성
        String accessToken  = JwtUtil.createAccessToken(user.getUserId(), user.getEmail());

        String refreshToken = JwtUtil.createRefreshToken(user.getUserId());

        return new SigninResponseDto(accessToken, refreshToken);
    }
}
