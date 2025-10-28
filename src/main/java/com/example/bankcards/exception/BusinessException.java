package com.example.bankcards.exception;

import lombok.Getter;

/**
 * Базовый класс для всех кастомных исключений в приложении.
 * <p>
 * Является непроверяемым исключением (наследуется от {@link RuntimeException}),
 * что упрощает его обработку в коде бизнес-логики.
 * </p>
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * Создает новый экземпляр BusinessException с указанным сообщением.
     *
     * @param message Сообщение об ошибке.
     */
    public BusinessException(String message) {
        super(message);
    }
}
