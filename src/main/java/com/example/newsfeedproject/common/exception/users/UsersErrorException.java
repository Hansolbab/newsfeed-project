package com.example.newsfeedproject.common.exception.users;

import com.example.newsfeedproject.common.exception.ErrorException;
import lombok.Getter;

@Getter
public class UsersErrorException extends ErrorException {

    private final UsersErrorCode usersErrorCode;

    public UsersErrorException(UsersErrorCode usersErrorCode) {
        super(usersErrorCode.getMessage() , usersErrorCode.getStatus());
        this.usersErrorCode = usersErrorCode;
    }
}
