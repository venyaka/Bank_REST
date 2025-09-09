package com.example.bankcards.dto.response;

import com.example.bankcards.entity.CardBlockRequest;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardBlockRequestRespDTO {

    private Long id;

    private Long cardId;

    private String cardMaskedNumber;

    private Long userId;

    private String userEmail;

    private CardBlockRequest.Status status;

    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    private String adminComment;

    private Long adminId;

    private String adminEmail;

}
