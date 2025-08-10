package com.example.newsfeedproject.myinfo.service;

import com.example.newsfeedproject.auth.service.signin.SigninService;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SigninService signinService;

    @Transactional
    public void resetPassword(Authentication authentication, String oldPassword, String newPassword) {
        // 현재 로그인된 사용자 정보 가져오기
        //authentication.getName() String 반환해야 하는데
        // orElse 는 옵셔널 객체에서만 사용 가능
        Users user = usersRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));


        //현재 비밀번호가 일치하는 지 확인
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다.");
        }
        //새 비밀번호를 암호화하여 업데이트
        String encodedNewPassword = bCryptPasswordEncoder.encode(newPassword);

        //비밀번호 변경 후 저장
        usersRepository.save(user);
    }

}
