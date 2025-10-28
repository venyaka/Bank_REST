package com.example.bankcards.exception;

import com.example.bankcards.exception.errors.AuthorizedError;
import lombok.Getter;

/**
 * Исключение, выбрасываемое при ошибках авторизации или аутентификации.
 * <p>
 * Наследуется от {@link BusinessException} и используется для сигнализации
 * о проблемах, связанных с правами доступа или проверкой учетных данных пользователя.
 * Хранит имя ошибки из перечисления {@link AuthorizedError}.
 * </p>
 */
@Getter
public class AuthorizeException extends BusinessException {

    /**
     * Имя ошибки, соответствующее одному из значений в {@link AuthorizedError}.
     */
    private final String errorName;

    /**
     * Создает новый экземпляр исключения на основе предопределенной ошибки авторизации.
     *
     * @param authorizedError Элемент перечисления {@link AuthorizedError}, содержащий сообщение и имя ошибки.
     */
    public AuthorizeException(AuthorizedError authorizedError) {
        super(authorizedError.getMessage());
        this.errorName = authorizedError.name();
    }

    /**
     * Создает новый экземпляр исключения с кастомным сообщением и именем ошибки.
     *
     * @param message   Пользовательское сообщение об ошибке.
     * @param errorName Пользовательское имя ошибки.
     */
    public AuthorizeException(String message, String errorName) {
        super(message);
        this.errorName = errorName;
    }
}
