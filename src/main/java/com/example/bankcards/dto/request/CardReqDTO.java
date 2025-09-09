package com.example.bankcards.dto.request;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardReqDTO {
    @NotBlank
    @Size(min = 16, max = 16, message = "Card number must be exactly 16 characters long")
    private String cardNumber;

    @NotNull
    private LocalDate expireDate;

    @NotNull
    private CardStatus status;

    @NotNull
    private BigDecimal balance;
}

