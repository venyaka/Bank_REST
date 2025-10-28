package com.example.bankcards.service.impl;

import com.example.bankcards.dto.response.CardBlockRequestRespDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.errors.BadRequestError;
import com.example.bankcards.exception.errors.NotFoundError;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.CardBlockRequestService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для управления запросами на блокировку карт.
 */
@Service
@RequiredArgsConstructor
public class CardBlockRequestServiceImpl implements CardBlockRequestService {

    private final CardBlockRequestRepository blockRequestRepository;
    private final CardRepository cardRepository;
    private final CardService cardService;
    private final CardEncryptor cardEncryptor;

    /**
     * {@inheritDoc}
     */
    @Override
    public CardBlockRequestRespDTO createBlockRequest(Long cardId, User user) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException(NotFoundError.CARD_NOT_FOUND));
        if (!card.getOwner().equals(user)) {
            throw new BadRequestException(BadRequestError.NO_ACCESS);
        }

        boolean alreadyRequested = blockRequestRepository.findByUser(user).stream()
                .anyMatch(r -> r.getCard().equals(card) && r.getStatus() == CardBlockRequest.Status.PENDING);
        if (alreadyRequested) {
            throw new BadRequestException(BadRequestError.BLOCK_REQUEST_ALREADY_EXISTS);
        }
        CardBlockRequest request = new CardBlockRequest();
        request.setCard(card);
        request.setUser(user);
        request.setStatus(CardBlockRequest.Status.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        blockRequestRepository.save(request);
        return toDto(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CardBlockRequestRespDTO> getUserBlockRequests(User user) {
        return blockRequestRepository.findByUser(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CardBlockRequestRespDTO> getAllBlockRequests() {
        return blockRequestRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardBlockRequestRespDTO approveBlockRequest(Long requestId, User admin, String comment) {
        CardBlockRequest request = blockRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(NotFoundError.BLOCK_REQUEST_NOT_FOUND));
        if (request.getStatus() != CardBlockRequest.Status.PENDING) {
            throw new BadRequestException(BadRequestError.BLOCK_REQUEST_ALREADY_EXISTS);
        }

        if (admin.getRoles().stream().noneMatch(r -> r.name().equals("ADMIN"))) {
            throw new BadRequestException(BadRequestError.NO_ACCESS);
        }
        request.setStatus(CardBlockRequest.Status.APPROVED);
        request.setProcessedAt(LocalDateTime.now());
        request.setAdminComment(comment);
        request.setAdmin(admin);
        blockRequestRepository.save(request);

        cardService.blockCard(request.getCard().getId(), admin);
        return toDto(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardBlockRequestRespDTO rejectBlockRequest(Long requestId, User admin, String comment) {
        CardBlockRequest request = blockRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(NotFoundError.BLOCK_REQUEST_NOT_FOUND));
        if (request.getStatus() != CardBlockRequest.Status.PENDING) {
            throw new BadRequestException(BadRequestError.BLOCK_REQUEST_ALREADY_PROCESSED);
        }
        if (admin.getRoles().stream().noneMatch(r -> r.name().equals("ADMIN"))) {
            throw new BadRequestException(BadRequestError.NO_ACCESS);
        }
        request.setStatus(CardBlockRequest.Status.REJECTED);
        request.setProcessedAt(LocalDateTime.now());
        request.setAdminComment(comment);
        request.setAdmin(admin);
        blockRequestRepository.save(request);
        return toDto(request);
    }

    /**
     * Конвертирует сущность CardBlockRequest в DTO.
     *
     * @param request Сущность для конвертации.
     * @return DTO с данными запроса.
     */
    private CardBlockRequestRespDTO toDto(CardBlockRequest request) {
        CardBlockRequestRespDTO dto = new CardBlockRequestRespDTO();
        dto.setId(request.getId());
        dto.setCardId(request.getCard().getId());
        dto.setCardMaskedNumber(maskCardNumber(decryptCardNumber(request.getCard().getCardNumber())));
        dto.setUserId(request.getUser().getId());
        dto.setUserEmail(request.getUser().getEmail());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setProcessedAt(request.getProcessedAt());
        dto.setAdminComment(request.getAdminComment());
        dto.setAdminId(request.getAdmin() != null ? request.getAdmin().getId() : null);
        dto.setAdminEmail(request.getAdmin() != null ? request.getAdmin().getEmail() : null);
        return dto;
    }

    /**
     * Маскирует номер карты.
     *
     * @param cardNumber Расшифрованный номер карты.
     * @return Маскированный номер.
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }

    /**
     * Расшифровывает номер карты.
     *
     * @param encryptedCardNumber Зашифрованный номер.
     * @return Расшифрованный номер.
     */
    private String decryptCardNumber(String encryptedCardNumber) {
        return cardEncryptor.decrypt(encryptedCardNumber);
    }
}
