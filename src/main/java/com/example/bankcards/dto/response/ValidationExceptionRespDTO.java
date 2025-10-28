package com.example.bankcards.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для ответа с информацией об ошибках валидации.
 * Расширяет стандартный ответ об ошибке, добавляя список конкретных нарушений.
 */
@Data
public class ValidationExceptionRespDTO {

    /**
     * Временная метка возникновения ошибки.
     */
    private LocalDateTime timestamp;

    /**
     * HTTP-статус код ответа.
     */
    private Long status;

    /**
     * Краткое описание типа ошибки (например, "Bad Request").
     */
    private String error;

    /**
     * Общее сообщение об ошибке валидации.
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

    /**
     * Список конкретных ошибок валидации полей.
     */
    private List<ConstraintFailRespDTO> constraintFailRespDTOList;
}
