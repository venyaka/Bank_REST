package com.example.bankcards.exception;

import com.example.bankcards.exception.errors.BadRequestError;
import lombok.Getter;

/**
 * Исключение, выбрасываемое при некорректных запросах от клиента (HTTP 400 Bad Request).
 * <p>
 * Наследуется от {@link BusinessException} и используется для сигнализации
 * о проблемах, связанных с неверными данными в запросе.
 * Хранит имя ошибки из перечисления {@link BadRequestError}.
 * </p>
 */
@Getter
public class BadRequestException extends BusinessException {

    /**
     * Имя ошибки, соответствующее одному из значений в {@link BadRequestError}.
     */
    private final String errorName;

    /**
     * Создает новый экземпляр исключения на основе предопределенной ошибки некорректного запроса.
     *
     * @param badRequestError Элемент перечисления {@link BadRequestError}, содержащий сообщение и имя ошибки.
     */
    public BadRequestException(BadRequestError badRequestError) {
        super(badRequestError.getMessage());
        errorName = badRequestError.name();
    }

    /**
     * Создает новый экземпляр исключения с кастомным сообщением и именем ошибки.
     *
     * @param message   Пользовательское сообщение об ошибке.
     * @param errorName Пользовательское имя ошибки.
     */
    public BadRequestException(String message, String errorName) {
        super(message);
        this.errorName = errorName;
    }
}
