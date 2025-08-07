package com.example.newsfeedproject.catagory.entity;

public enum Category {
    KOREAN,
    JAPANESE,
    CHINESE,
    WESTERN,
    OTHERS;

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
