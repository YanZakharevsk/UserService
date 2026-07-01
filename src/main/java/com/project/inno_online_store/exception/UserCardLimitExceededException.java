package com.project.inno_online_store.exception;

import org.springframework.http.HttpStatus;

public class UserCardLimitExceededException extends BaseException {
    public UserCardLimitExceededException(Long userId)
    {
        super("User with id " + userId + " can not have more than 5 cards", "USER_CARD_LIMIT_EXCEEDED", HttpStatus.CONFLICT);
    }
}
