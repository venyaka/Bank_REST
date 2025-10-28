package com.example.bankcards.dto.response;

import com.example.bankcards.entity.CardStatus;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO для ответа с информацией о балансе и статусе карты.
 */
@Data
public class CardBalanceRespDTO {
    /**
     * Идентификатор карты.
     */
    private Long cardId;
    /**
     * Текущий баланс карты.
     */
    private BigDecimal balance;
    /**
     * Текущий статус карты (например, ACTIVE, BLOCKED).
     */
    private CardStatus status;
}

