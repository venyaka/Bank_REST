package com.example.bankcards.exception.errors;

public enum NotFoundError {

    CARD_NOT_FOUND("Карта не найдена"),
    BLOCK_REQUEST_NOT_FOUND("Запрос на блокировку не найден."),
    USER_NOT_FOUND("Пользователь не был найден");

    private String message;
    private final String message;

    NotFoundError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
