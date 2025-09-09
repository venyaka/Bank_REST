package com.example.bankcards.dto.response;

import com.example.bankcards.entity.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardRespDTO {

    private Long id;

    private String maskedCardNumber;

    private String ownerEmail;

    private LocalDate expireDate;

    private CardStatus status;

    private BigDecimal balance;

}

