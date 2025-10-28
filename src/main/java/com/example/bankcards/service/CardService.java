package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateCardReqDTO;
import com.example.bankcards.dto.request.TransferReqDTO;
import com.example.bankcards.dto.response.CardBalanceRespDTO;
import com.example.bankcards.dto.response.CardRespDTO;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервис для управления банковскими картами.
 * <p>
 * Предоставляет функциональность для создания, блокировки, активации, удаления карт,
 * а также для получения информации о картах и выполнения переводов.
 * </p>
 */
public interface CardService {

    /**
     * Создает новую банковскую карту для указанного пользователя.
     *
     * @param createCardReqDTO DTO с ID владельца карты.
     * @return DTO с информацией о созданной карте.
     * @throws com.example.bankcards.exception.NotFoundException если пользователь-владелец не найден.
     */
    CardRespDTO createCard(CreateCardReqDTO createCardReqDTO);

    /**
     * Блокирует карту. Доступно только владельцу карты или администратору.
     *
     * @param cardId    ID карты для блокировки.
     * @param requester Пользователь, выполняющий операцию.
     * @throws com.example.bankcards.exception.NotFoundException если карта не найдена.
     * @throws com.example.bankcards.exception.BadRequestException если у пользователя нет прав на это действие.
     */
    void blockCard(Long cardId, User requester);

    /**
     * Активирует карту. Доступно только владельцу карты или администратору.
     *
     * @param cardId    ID карты для активации.
     * @param requester Пользователь, выполняющий операцию.
     * @throws com.example.bankcards.exception.NotFoundException если карта не найдена.
     * @throws com.example.bankcards.exception.BadRequestException если у пользователя нет прав на это действие.
     */
    void activateCard(Long cardId, User requester);

    /**
     * Удаляет карту. Доступно только владельцу карты или администратору.
     *
     * @param cardId    ID карты для удаления.
     * @param requester Пользователь, выполняющий операцию.
     * @throws com.example.bankcards.exception.NotFoundException если карта не найдена.
     * @throws com.example.bankcards.exception.BadRequestException если у пользователя нет прав на это действие.
     */
    void deleteCard(Long cardId, User requester);

    /**
     * Получает информацию о карте по ее ID.
     * Доступно только владельцу карты или администратору.
     *
     * @param cardId    ID карты.
     * @param requester Пользователь, выполняющий операцию.
     * @return DTO с информацией о карте.
     * @throws com.example.bankcards.exception.NotFoundException если карта не найдена.
     * @throws com.example.bankcards.exception.BadRequestException если у пользователя нет прав на это действие.
     */
    CardRespDTO getCardById(Long cardId, User requester);

    /**
     * Получает список всех карт, принадлежащих указанному пользователю.
     *
     * @param owner Владелец карт.
     * @return Список DTO с информацией о картах.
     */
    List<CardRespDTO> getCardsByOwner(User owner);

    /**
     * Выполняет постраничный поиск по картам пользователя.
     *
     * @param owner    Владелец карт.
     * @param query    Строка для поиска по номеру или статусу карты.
     * @param pageable Параметры пагинации.
     * @return Страница с найденными картами.
     */
    Page<CardRespDTO> searchCards(User owner, String query, Pageable pageable);

    /**
     * Выполняет перевод средств между двумя картами одного пользователя.
     *
     * @param transferReqDTO DTO с деталями перевода.
     * @param requester      Пользователь, выполняющий операцию (должен быть владельцем обеих карт).
     * @throws com.example.bankcards.exception.BadRequestException если нарушены бизнес-правила (недостаточно средств, карты заблокированы и т.д.).
     * @throws com.example.bankcards.exception.NotFoundException если одна из карт не найдена.
     */
    void transferBetweenCards(TransferReqDTO transferReqDTO, User requester);

    /**
     * Получает список всех карт в системе (только для администраторов).
     *
     * @return Список DTO всех карт.
     */
    List<CardRespDTO> getAllCards();

    /**
     * Получает баланс и статус карты по ее ID.
     * Доступно только владельцу карты или администратору.
     *
     * @param cardId    ID карты.
     * @param requester Пользователь, выполняющий операцию.
     * @return DTO с балансом и статусом карты.
     * @throws com.example.bankcards.exception.NotFoundException если карта не найдена.
     * @throws com.example.bankcards.exception.BadRequestException если у пользователя нет прав на это действие.
     */
    CardBalanceRespDTO getCardBalance(Long cardId, User requester);

    /**
     * Обновляет баланс карты.
     * ВНИМАНИЕ: Этот метод предназначен только для администраторов.
     *
     * @param cardId     ID карты.
     * @param newBalance Новый баланс.
     * @param requester  Пользователь, выполняющий операцию.
     * @throws com.example.bankcards.exception.NotFoundException если карта не найдена.
     * @throws com.example.bankcards.exception.BadRequestException если у пользователя нет прав на это действие.
     */
    void updateCardBalance(Long cardId, BigDecimal newBalance, User requester);
}
