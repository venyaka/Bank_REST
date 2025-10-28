package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Представляет сеанс пользователя.
 */
@Entity
@Table(name = "user_session")
@Getter
@Setter
@NoArgsConstructor
public class UserSession {

    /**
     * Уникальный идентификатор сеанса пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь, связанный с этим сеансом.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /**
     * IP-адрес, с которого вошел пользователь.
     */
    @Column(name = "ip_address")
    private String ipAddress;

    /**
     * Город, из которого вошел пользователь.
     */
    @Column(name = "city")
    private String city;

    /**
     * Строка user-agent клиента.
     */
    @Column(name = "user_agent")
    private String userAgent;

    /**
     * Название операционной системы клиента.
     */
    @Column(name = "os_name")
    private String osName;

    /**
     * Тип устройства, используемого клиентом.
     */
    @Column(name = "device_type")
    private String deviceType;

    /**
     * Временная метка начала сеанса.
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * Временная метка окончания сеанса.
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;
}
