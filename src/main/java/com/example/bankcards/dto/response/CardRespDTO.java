package com.example.bankcards.dto.response;

import com.example.bankcards.entity.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO для ответа с основной информацией о банковской карте.
 */
@Data
public class CardRespDTO {

    /**
     * Идентификатор карты.
     */
    private Long id;

    /**
     * Маскированный номер карты (видны только последние 4 цифры).
     */
    private String maskedCardNumber;

    /**
     * Email владельца карты.
     */
    private String ownerEmail;

    /**
     * Дата окончания срока действия карты.
     */
    private LocalDate expireDate;

    /**
     * Текущий статус карты (например, ACTIVE, BLOCKED).
     */
    private CardStatus status;

    /**
     * Текущий баланс карты.
     */
    private BigDecimal balance;

}

