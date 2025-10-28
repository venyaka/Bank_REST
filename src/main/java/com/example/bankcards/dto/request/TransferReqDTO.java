package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO для запроса на перевод средств между картами.
 */
@Data
public class TransferReqDTO {
    /**
     * Идентификатор карты, с которой осуществляется перевод.
     */
    @NotNull
    private Long fromCardId;
    /**
     * Идентификатор карты, на которую осуществляется перевод.
     */
    @NotNull
    private Long toCardId;
    /**
     * Сумма перевода. Должна быть положительным числом.
     */
    @NotNull
    @Positive(message = "Сумма перевода должна быть положительной")
    private BigDecimal amount;
}

