package com.example.bankcards.dto.response;

import lombok.Data;
import com.example.bankcards.entity.CardStatus;
import java.math.BigDecimal;

@Data
public class CardBalanceRespDTO {
    private Long cardId;
    private BigDecimal balance;
    private CardStatus status;
}

