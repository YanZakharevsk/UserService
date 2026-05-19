package com.project.inno_online_store.exception;

public class UserCardLimitExceededException extends RuntimeException {
    public UserCardLimitExceededException(Long userId)
    {
        super("User with id " + userId + " can not have more than 5 cards");
    }
}
