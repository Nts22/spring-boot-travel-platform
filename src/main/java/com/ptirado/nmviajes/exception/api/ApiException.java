package com.ptirado.nmviajes.exception.api;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public abstract class ApiException extends RuntimeException {

    private final String messageKey;
    private final HttpStatus status;
    private final Object[] args;

    public ApiException(String messageKey, HttpStatus status, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.status = status;
        this.args = args;
    }
}

