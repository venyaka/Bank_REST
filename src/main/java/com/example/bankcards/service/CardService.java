package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateCardReqDTO;
import com.example.bankcards.dto.request.TransferReqDTO;
import com.example.bankcards.dto.response.CardBalanceRespDTO;
import com.example.bankcards.dto.response.CardRespDTO;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardService {

    CardRespDTO createCard(CreateCardReqDTO createCardReqDTO);

    void blockCard(Long cardId, User requester);

    void activateCard(Long cardId, User requester);

    void deleteCard(Long cardId, User requester);

    CardRespDTO getCardById(Long cardId, User requester);

    List<CardRespDTO> getCardsByOwner(User owner);

    Page<CardRespDTO> searchCards(User owner, String query, Pageable pageable);

    void transferBetweenCards(TransferReqDTO transferReqDTO, User requester);

    List<CardRespDTO> getAllCards();

    CardBalanceRespDTO getCardBalance(Long cardId, User requester);

    /**
     * Тестовый метод для изменения баланса карты (использовать только для тестирования)
     */
    void updateCardBalance(Long cardId, java.math.BigDecimal newBalance, User requester);
}
