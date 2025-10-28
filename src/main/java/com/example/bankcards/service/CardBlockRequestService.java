package com.example.bankcards.service;

import com.example.bankcards.dto.response.CardBlockRequestRespDTO;
import com.example.bankcards.entity.User;

import java.util.List;

/**
 * Сервис для управления запросами на блокировку карт.
 * <p>
 * Позволяет пользователям создавать запросы на блокировку своих карт,
 * а администраторам — просматривать, одобрять и отклонять эти запросы.
 * </p>
 */
public interface CardBlockRequestService {

    /**
     * Создает новый запрос на блокировку карты.
     *
     * @param cardId ID карты, для которой создается запрос.
     * @param user   Пользователь, инициирующий запрос.
     * @return DTO с информацией о созданном запросе.
     * @throws com.example.bankcards.exception.NotFoundException если карта не найдена.
     * @throws com.example.bankcards.exception.BadRequestException если карта уже заблокирована или запрос уже существует.
     */
    CardBlockRequestRespDTO createBlockRequest(Long cardId, User user);

    /**
     * Получает список всех запросов на блокировку для указанного пользователя.
     *
     * @param user Пользователь, чьи запросы нужно получить.
     * @return Список DTO с информацией о запросах.
     */
    List<CardBlockRequestRespDTO> getUserBlockRequests(User user);

    /**
     * Получает список всех запросов на блокировку в системе (только для администраторов).
     *
     * @return Список DTO всех запросов на блокировку.
     */
    List<CardBlockRequestRespDTO> getAllBlockRequests();

    /**
     * Одобряет запрос на блокировку карты. Доступно только администраторам.
     *
     * @param requestId ID запроса на блокировку.
     * @param admin     Администратор, выполняющий операцию.
     * @param comment   Комментарий администратора.
     * @return DTO с обновленной информацией об одобренном запросе.
     * @throws com.example.bankcards.exception.NotFoundException если запрос не найден.
     * @throws com.example.bankcards.exception.BadRequestException если запрос уже обработан.
     */
    CardBlockRequestRespDTO approveBlockRequest(Long requestId, User admin, String comment);

    /**
     * Отклоняет запрос на блокировку карты. Доступно только администраторам.
     *
     * @param requestId ID запроса на блокировку.
     * @param admin     Администратор, выполняющий операцию.
     * @param comment   Комментарий администратора.
     * @return DTO с обновленной информацией об отклоненном запросе.
     * @throws com.example.bankcards.exception.NotFoundException если запрос не найден.
     * @throws com.example.bankcards.exception.BadRequestException если запрос уже обработан.
     */
    CardBlockRequestRespDTO rejectBlockRequest(Long requestId, User admin, String comment);
}
