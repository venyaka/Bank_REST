package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Представляет запрос на блокировку карты.
 */
@Entity
@Table(name = "card_block_request")
@Getter
@Setter
@NoArgsConstructor
public class CardBlockRequest {

    /**
     * Уникальный идентификатор запроса на блокировку.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Карта для блокировки.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card;

    /**
     * Пользователь, инициировавший запрос на блокировку.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Администратор, обработавший запрос на блокировку.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;

    /**
     * Статус запроса на блокировку (например, PENDING, APPROVED, REJECTED).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    /**
     * Временная метка создания запроса на блокировку.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Временная метка обработки запроса на блокировку.
     */
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    /**
     * Комментарий администратора относительно запроса на блокировку.
     */
    @Column(name = "admin_comment")
    private String adminComment;

    /**
     * Представляет статус запроса на блокировку карты.
     */
    public enum Status {
        PENDING, APPROVED, REJECTED
    }
}
