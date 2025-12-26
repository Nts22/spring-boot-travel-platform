package com.ptirado.nmviajes.exception.api;

import org.springframework.http.HttpStatus;

public class ConflictException extends ApiException {
    public ConflictException(String messageKey, Object... args) {
        super(messageKey, HttpStatus.CONFLICT, args);
    }
}
