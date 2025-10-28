package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO для запроса на создание новой банковской карты.
 */
@Data
public class CreateCardReqDTO {

    /**
     * Идентификатор пользователя-владельца карты.
     */
    @NotNull
    private Long ownerId;
}

