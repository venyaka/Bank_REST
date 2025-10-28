package com.example.bankcards.exception.errors;

/**
 * Перечисление, определяющее типы ошибок авторизации и аутентификации.
 * <p>
 * Каждый элемент перечисления представляет собой конкретную ошибку,
 * связанную с процессом входа пользователя в систему, и содержит
 * соответствующее сообщение для пользователя на русском языке.
 * </p>
 */
public enum AuthorizedError {

    NOT_AUTHENTICATED("Пользователь не авторизован"),
    TOKEN_WAS_EXPIRED("Срок действия токена истек"),
    BAD_CREDENTIALS("Неверные данные для авторизации"),
    USER_NOT_VERIFY("Пользователь не верифицирован"),
    USER_WITH_THIS_EMAIL_NOT_FOUND("Пользователь с данным email не найден в системе"),
    NOT_CORRECT_PASSWORD("Неверный пароль"),
    USER_IS_DELETED("Пользователь удален"),
    NOT_CORRECT_TOKEN("Неверный токен доступа");

    /**
     * Сообщение об ошибке, предназначенное для отображения пользователю.
     */
    private final String message;

    /**
     * Конструктор для инициализации элемента перечисления с сообщением.
     *
     * @param message Сообщение об ошибке.
     */
    AuthorizedError(String message) {
        this.message = message;
    }

    /**
     * Возвращает сообщение об ошибке.
     *
     * @return Строка с текстом ошибки.
     */
    public String getMessage() {
        return message;
    }
}
