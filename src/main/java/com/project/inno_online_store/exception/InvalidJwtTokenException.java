package com.project.inno_online_store.exception;

import org.springframework.http.HttpStatus;

public class InvalidJwtTokenException extends BaseException {
    public InvalidJwtTokenException(String message) {
        super(message, "INVALID_JWT_TOKEN" , HttpStatus.UNAUTHORIZED);
    }
}
