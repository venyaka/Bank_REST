package com.example.bankcards.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для ответа с информацией о бизнес-исключении.
 * Используется для стандартизации формата ошибок в API.
 */
@Data
public class BusinessExceptionRespDTO {

    /**
     * Временная метка возникновения ошибки.
     */
    private LocalDateTime timestamp;

    /**
     * HTTP-статус код ответа.
     */
    private Long status;

    /**
     * Краткое описание типа ошибки (например, "Not Found").
     */
    private String error;

    /**
     * Подробное сообщение об ошибке.
     */
    private String message;

    /**
     * Путь запроса, на котором произошла ошибка.
     */
    private String path;

    /**
     * Отладочная информация (может отсутствовать в продакшене).
     */
    private String debugInfo;
}
