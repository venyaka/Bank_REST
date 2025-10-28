package com.example.bankcards.exception;

import com.example.bankcards.exception.errors.EncryptionError;
import lombok.Getter;

/**
 * Исключение, выбрасываемое при некорректных запросах от клиента (HTTP 400 Bad Request).
 * <p>
 * Наследуется от {@link BusinessException} и используется для сигнализации
 * о проблемах, связанных с неверными данными в запросе.
 * Хранит имя ошибки из перечисления {@link EncryptionError}.
 * </p>
 */
@Getter
public class EncryptionException extends BusinessException {

    /**
     * Имя ошибки, соответствующее одному из значений в {@link EncryptionError}.
     */
    private final String errorName;

    /**
     * Создает новый экземпляр исключения на основе предопределенной ошибки некорректного запроса.
     *
     * @param encryptionError Элемент перечисления {@link EncryptionError}, содержащий сообщение и имя ошибки.
     */
    public EncryptionException(EncryptionError encryptionError) {
        super(encryptionError.getMessage());
        errorName = encryptionError.name();
    }

    /**
     * Создает новый экземпляр исключения с кастомным сообщением и именем ошибки.
     *
     * @param message   Пользовательское сообщение об ошибке.
     * @param errorName Пользовательское имя ошибки.
     */
    public EncryptionException(String message, String errorName) {
        super(message);
        this.errorName = errorName;
    }
}
