package com.example.newsfeedproject.category.entity;
import static com.example.newsfeedproject.common.exception.feeds.FeedsErrorCode.*;

import com.example.newsfeedproject.common.exception.feeds.FeedsErrorException;
import com.fasterxml.jackson.annotation.JsonCreator; // JsonCreator 임포트
import lombok.Getter;
import java.util.Arrays;

@Getter
public enum Category {
    KOREAN("한식"),
    JAPANESE("일식"),
    CHINESE("중식"),
    WESTERN("양식"),
    OTHERS("기타");

    public final String koreanName;

    // 생성자 추가 (Enum 이름과 한글 이름 매핑)
    Category(String koreanName) {
        this.koreanName = koreanName;
    }

    // Enum 값을 한글 문자열로 변환 (응답 시 사용)
    @Override
    public String toString() {
        return this.koreanName; // toString은 한글명 반환
    }

    // 문자열(영어 Enum 이름 또는 한글명)을 Category Enum으로 변환하는 메서드
    // @JsonCreator 어노테이션으로 Jackson이 이 메서드를 역직렬화에 사용하도록 지시
    @JsonCreator
    public static Category fromString(String text) {
        for (Category c : Category.values()) {
            // 대소문자 구분 없이 Enum 이름 또는 한글 이름과 일치하는지 확인
            if (c.name().equalsIgnoreCase(text) || c.koreanName.equalsIgnoreCase(text)) {
                return c;
            }
        }
        // 일치하는 카테고리가 없을 경우 예외 발생 (Bad Request)
        throw new FeedsErrorException(NOT_FOUND_CATEGORY);
    }

    //문자열-> 이넘 타입으로 변경
    public static Category sortedType(String name){
        return Arrays.stream(Category.values())
                .filter(category -> category.getKoreanName().contains(name) || category.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() ->  new FeedsErrorException(NOT_FOUND_CATEGORY));
    }
}