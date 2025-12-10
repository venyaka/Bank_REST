package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Представляет сущность банковской карты.
 */
@Entity
@Table(name = "card")
@Getter
@Setter
@NoArgsConstructor
public class Card {

    /**
     * Уникальный идентификатор карты.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Номер карты.
     */
    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    /**
     * Владелец карты.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * Срок действия карты.
     */
    @Column(name = "expire_date", nullable = false)
    private LocalDate expireDate;

    /**
     * Статус карты (например, ACTIVE, BLOCKED, EXPIRED).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;

    /**
     * Баланс карты.
     */
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return id != null && Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

