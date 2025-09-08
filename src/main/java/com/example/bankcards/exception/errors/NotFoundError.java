package com.example.bankcards.exception.errors;

public enum NotFoundError {

    USER_NOT_FOUND("Пользователь не был найден");

    private String message;

    NotFoundError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
