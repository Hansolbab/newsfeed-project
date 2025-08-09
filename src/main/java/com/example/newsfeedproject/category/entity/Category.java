package com.example.newsfeedproject.category.entity;

//이넘 클래스
public enum Category {
    KOREAN,
    JAPANESE,
    CHINESE,
    WESTERN,
    OTHERS;


    //enum 한글문자열로 변경 -> feed 관련 response body에 사용하면 됩니다.
    public String toString() {

       return switch (this){
            case KOREAN -> "한식";
            case JAPANESE -> "일식";
            case CHINESE ->  "중식";
            case WESTERN ->  "양식";
            case OTHERS ->  "기타";
        };
    }


}
