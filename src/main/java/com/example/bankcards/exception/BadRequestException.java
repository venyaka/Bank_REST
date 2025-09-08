package com.example.bankcards.exception;

import lombok.Getter;
import com.example.bankcards.exception.errors.BadRequestError;

@Getter
public class BadRequestException extends BusinessException {

    private String errorName;

    public BadRequestException(BadRequestError badRequestError) {
        super(badRequestError.getMessage());
        errorName = badRequestError.name();
    }

    public BadRequestException(String message, String errorName) {
        super(message);
        this.errorName = errorName;
    }
}
