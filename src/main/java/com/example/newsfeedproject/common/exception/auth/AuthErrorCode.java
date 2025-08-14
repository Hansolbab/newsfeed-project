package com.example.newsfeedproject.common.exception.auth;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
//TODO: 마이인포의 비밀번호
public enum AuthErrorCode {
    // 로그인
    USER_NOT_REGISTERED(400, "가입된 사용자가 아닙니다."),// 로그인시
    USER_NOT_FOUND(404, "사용자 정보를 찾을 수 없습니다."),

    //회원가입
    USER_ALREADY_EXISTS(409, "이미 사용 중인 아이디입니다."),  
    EMAIL_ALREADY_EXISTS(409, "이미 가입된 이메일입니다."),

    // 비밀번호 관련
    PASSWORD_NOT_MATCH(409, "비밀번호가 일치하지 않습니다."),// 로그인시, 비밀번호 변경 시
    CURRENT_PASSWORD_NOT_MATCH(400, "현재 비밀번호가 일치하지 않습니다."), // 탈퇴할때
    NEW_PASSWORD_SAME_AS_OLD(400, "새 비밀번호가 기존 비밀번호와 같습니다."), // 비밀번호 변경 시

    // 프로필/전화번호 관련
    PROFILE_IMAGE_SAME(409, "현재 프로필이미지와 동일합니다."),
    PHONE_NUMBER_SAME(409, "현재 전화번호와 동일합니다.");


    private final int status;
    private final String message;

    AuthErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    //new IllegalArgumentException("가입된 사용자가 아닙니다."));
    //throw new IllegalArgumentException("비밀번호가 일치하지 않습니다."); => 비밀번호 변경도 같이
    //throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
    //throw new IllegalArgumentException("이미 가입된 이메일입니다.");
    //new ResponseStatusException(HttpStatus.NOT_FOUND,"사용자 정보를 찾을 수 없습니다."));
    //throw new ResponseStatusException(HttpStatus.CONFLICT, "현재 프로필이미지와 동일합니다.");
    //new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    //new ResponseStatusException(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다.");
    //throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "새 비밀번호가 기존 비밀번호와 같습니다.");
    //new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    //throw new ResponseStatusException(HttpStatus.CONFLICT, "현재 전화번호와 동일합니다.");



}
