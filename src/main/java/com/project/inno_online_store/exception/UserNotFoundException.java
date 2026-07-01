package com.project.inno_online_store.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
