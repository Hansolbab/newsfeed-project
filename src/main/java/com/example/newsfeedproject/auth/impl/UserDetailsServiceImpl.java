package com.example.newsfeedproject.auth.impl;

import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
// userName(로그인 식별자 즉, 아이디) 기반으로 DB에서 유저를 가져오는 클래스
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsersRepository usersRepository;

    //Spring Security 로그인 처리 시 호출됨
    //@param username: 여기선 email을 받도록 설정
    @Override
    public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = usersRepository.findByEmail(email)
                // 탈퇴한 계정 차단, deleted=true면 제외
                .filter(u->!Boolean.TRUE.equals(u.getDeleted()))
                .orElseThrow(() -> new UsernameNotFoundException("가입된 사용자가 아닙니다: " + email));
        return new UserDetailsImpl(user);
    }
}
