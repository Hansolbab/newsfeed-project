package com.example.newsfeedproject.myinfo.service;

import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProfileImageService {
    private final UsersRepository usersRepository;

    //TODO: 이미 UserEntity에 기본값 설정되어있는데 왜 여기에도 들어가야 하는가?
    // UserEntity 기본값과 동일해야 함 
    private static final String PLACEHOLDER_URL = "https://via.placeholder.com/150";

    // 프로필사진 수정
    @Transactional
    public void updateProfileImageUrl(Long meId, String newprofileImageUrl) {
        Users user = usersRepository.findById(meId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        //409
        if (newprofileImageUrl.equals(user.getProfileImageUrl())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "현재 프로필이미지와 동일합니다.");

            //변경
        }
        user.setProfileImageUrl(newprofileImageUrl);

    }
}