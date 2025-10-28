package com.example.bankcards.exception;

import lombok.Getter;
import com.example.bankcards.exception.errors.AuthorizedError;

@Getter
public class AuthorizeException extends BusinessException {

    private String errorName;
    private final String errorName;

    public AuthorizeException(AuthorizedError authorizedError) {
        super(authorizedError.getMessage());
        this.errorName = authorizedError.name();
    }

    public AuthorizeException(String message, String errorName) {
        super(message);
        this.errorName = errorName;
    }
}
