package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferReqDTO {
    @NotNull
    private Long fromCardId;
    @NotNull
    private Long toCardId;
    @NotNull
    private BigDecimal amount;
}

