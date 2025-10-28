package com.example.bankcards.dto.request;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO с основными данными банковской карты.
 * Используется в запросах, где требуется передать информацию о карте.
 */
@Data
public class CardReqDTO {
    /**
     * Номер карты. Должен состоять ровно из 16 символов.
     */
    @NotBlank
    @Size(min = 16, max = 16, message = "Card number must be exactly 16 characters long")
    private String cardNumber;

    /**
     * Дата окончания срока действия карты.
     */
    @NotNull
    private LocalDate expireDate;

    /**
     * Статус карты (например, ACTIVE, BLOCKED).
     */
    @NotNull
    private CardStatus status;

    /**
     * Баланс карты. Должен быть положительным числом или нулем.
     */
    @NotNull
    private BigDecimal balance;
}

