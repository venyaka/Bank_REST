package com.example.bankcards.exception;

import com.example.bankcards.exception.errors.NotFoundError;
import lombok.Getter;

/**
 * Исключение, выбрасываемое, когда запрашиваемый ресурс не найден (HTTP 404 Not Found).
 * <p>
 * Наследуется от {@link BusinessException} и используется для сигнализации
 * о том, что сущность или ресурс не существуют в системе.
 * Хранит имя ошибки из перечисления {@link NotFoundError}.
 * </p>
 */
@Getter
public class NotFoundException extends BusinessException {

    /**
     * Имя ошибки, соответствующее одному из значений в {@link NotFoundError}.
     */
    private final String errorName;

    /**
     * Создает новый экземпляр исключения на основе предопределенной ошибки "не найдено".
     *
     * @param notFoundError Элемент перечисления {@link NotFoundError}, содержащий сообщение и имя ошибки.
     */
    public NotFoundException(NotFoundError notFoundError) {
        super(notFoundError.getMessage());
        errorName = notFoundError.name();
    }

    /**
     * Создает новый экземпляр исключения с кастомным сообщением и именем ошибки.
     *
     * @param message   Пользовательское сообщение об ошибке.
     * @param errorName Пользовательское имя ошибки.
     */
    public NotFoundException(String message, String errorName) {
        super(message);
        this.errorName = errorName;
    }
}
