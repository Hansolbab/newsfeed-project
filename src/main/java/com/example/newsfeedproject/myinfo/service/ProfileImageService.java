package com.example.newsfeedproject.myinfo.service;

import com.example.newsfeedproject.common.exception.auth.AuthErrorException;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.example.newsfeedproject.common.exception.auth.AuthErrorCode.*;
@Service
@RequiredArgsConstructor
public class ProfileImageService {
    private final UsersRepository usersRepository;

    //TODO: 이미 UserEntity에 기본값 설정되어있는데 왜 여기에도 들어가야 하는가?
    // 엔티티는 단지, 객체 생성 시점에만 적용되는거고 이건 삭제 시 채워줄 상수일 뿐
    private static final String PLACEHOLDER_URL = "https://via.placeholder.com/150";

    // 프로필사진 수정
    @Transactional
    public void updateProfileImageUrl(Long meId, String newProfileImageUrl) {
        Users user = usersRepository.findById(meId)
                .orElseThrow(() -> new AuthErrorException(USER_NOT_FOUND));

        //409
        if (newProfileImageUrl.equals(user.getProfileImageUrl())) {
            throw new AuthErrorException(PROFILE_IMAGE_SAME);
        }
        user.setProfileImageUrl(newProfileImageUrl);
    }

    //삭제
    @Transactional
    public void setPlaceholderUrl(Long meId) {
        Users user=usersRepository.findById(meId)
                .orElseThrow(()->new AuthErrorException(USER_NOT_FOUND));
        //만약 이미 기본값이면 넘어가고, 아니면 PLACEHOLDER_URL
        if(!PLACEHOLDER_URL.equals(user.getProfileImageUrl())) {
            user.setProfileImageUrl(PLACEHOLDER_URL);
        }
    }
}