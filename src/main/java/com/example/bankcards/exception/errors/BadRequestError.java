package com.example.bankcards.exception.errors;

public enum BadRequestError {

    NOT_CORRECT_PASSWORD("Неверный пароль"),
    NOT_CORRECT_REFRESH_TOKEN("Неверный рефреш токен"),
    USER_ALREADY_VERIFICATED("Пользователь уже был верифицирован"),
    USER_NOT_VERIFICATED("Пользователь не верифицирован"),
    NOT_CORRECT_VERIFICATION_CODE("Код верификации не корректен или не был запрошен"),
    USER_ALREADY_EXISTS("Пользователь с такой почтой уже существует"),
    NO_ACCESS("Нет доступа"),
    ONLY_OWN_CARDS_TRANSFER("Можно переводить только между своими картами"),
    INSUFFICIENT_FUNDS("Недостаточно средств"),
    FROM_CARD_BLOCKED("С карты-отправителя нельзя совершать переводы, так как она заблокирована"),
    TO_CARD_BLOCKED("На карту-получатель нельзя совершать переводы, так как она заблокирована"),
    FROM_CARD_EXPIRED("С карты-отправителя нельзя совершать операции, так как срок действия истёк"),
    TO_CARD_EXPIRED("На карту-получатель нельзя совершать операции, так как срок действия истёк"),
    BLOCK_REQUEST_ALREADY_EXISTS("Запрос на блокировку этой карты уже создан и ожидает обработки."),
    BLOCK_REQUEST_ALREADY_PROCESSED("Запрос уже обработан.");

    private String message;
    private final String message;

    BadRequestError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
