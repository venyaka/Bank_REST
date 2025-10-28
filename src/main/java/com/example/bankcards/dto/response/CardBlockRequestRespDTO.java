package com.example.bankcards.dto.response;

import com.example.bankcards.entity.CardBlockRequest;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для ответа с информацией о запросе на блокировку карты.
 */
@Data
public class CardBlockRequestRespDTO {

    /**
     * Идентификатор запроса на блокировку.
     */
    private Long id;

    /**
     * Идентификатор блокируемой карты.
     */
    private Long cardId;

    /**
     * Маскированный номер карты.
     */
    private String cardMaskedNumber;

    /**
     * Идентификатор пользователя, инициировавшего запрос.
     */
    private Long userId;

    /**
     * Email пользователя, инициировавшего запрос.
     */
    private String userEmail;

    /**
     * Статус запроса (например, PENDING, APPROVED, REJECTED).
     */
    private CardBlockRequest.Status status;

    /**
     * Время создания запроса.
     */
    private LocalDateTime createdAt;

    /**
     * Время обработки запроса администратором.
     */
    private LocalDateTime processedAt;

    /**
     * Комментарий администратора.
     */
    private String adminComment;

    /**
     * Идентификатор администратора, обработавшего запрос.
     */
    private Long adminId;

    /**
     * Email администратора, обработавшего запрос.
     */
    private String adminEmail;

}
