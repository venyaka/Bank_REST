package com.example.bankcards.exception.errors;

/**
 * Перечисление, определяющее типы ошибок, связанных с шифрованием.
 */
public enum EncryptionError {

    ENCRYPTION_KEY_NOT_FOUND("Ключ шифрования не получен из Vault"),
    INVALID_KEY_LENGTH("Неверная длина ключа для AES. Ожидается 16, 24 или 32 символа."),
    ENCRYPTION_FAILED("Ошибка шифрования номера карты"),
    DECRYPTION_FAILED("Ошибка дешифрования номера карты");

    /**
     * Сообщение об ошибке, предназначенное для отображения пользователю.
     */
    private final String message;

    /**
     * Конструктор для инициализации элемента перечисления с сообщением.
     *
     * @param message Сообщение об ошибке.
     */
    EncryptionError(String message) {
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

