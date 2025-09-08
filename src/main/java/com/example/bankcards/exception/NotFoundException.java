package com.example.bankcards.exception;

import lombok.Getter;
import com.example.bankcards.exception.errors.NotFoundError;

@Getter
public class NotFoundException extends BusinessException {

    private String errorName;

    public NotFoundException(NotFoundError notFoundError) {
        super(notFoundError.getMessage());
        errorName = notFoundError.name();
    }

    public NotFoundException(String message, String errorName) {
        super(message);
        this.errorName = errorName;
    }
}
