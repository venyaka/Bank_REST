package com.example.bankcards.dto.response;

import lombok.Data;

/**
 * DTO для представления информации о конкретном нарушении валидации.
 */
@Data
public class ConstraintFailRespDTO {

    /**
     * Имя поля, в котором произошла ошибка валидации.
     */
    private String fieldName;

    /**
     * Сообщение об ошибке валидации.
     */
    private String message;

    /**
     * Значение, которое не прошло валидацию.
     */
    private Object rejectedValue;

    /**
     * Код ошибки валидации.
     */
    private String code;
}
