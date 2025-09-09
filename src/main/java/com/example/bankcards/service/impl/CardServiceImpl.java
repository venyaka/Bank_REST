package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.CreateCardReqDTO;
import com.example.bankcards.dto.request.TransferReqDTO;
import com.example.bankcards.dto.response.CardBalanceRespDTO;
import com.example.bankcards.dto.response.CardRespDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.errors.BadRequestError;
import com.example.bankcards.exception.errors.NotFoundError;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final CardEncryptor cardEncryptor;

    private final UserRepository userRepository;

    @Override
    public CardRespDTO createCard(CreateCardReqDTO createCardReqDTO) {
        Optional<User> optionalUser = userRepository.findById(createCardReqDTO.getOwnerId());
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(NotFoundError.USER_NOT_FOUND);
        }
        User owner = optionalUser.get();

        Card card = new Card();
        card.setOwner(owner);
        String cardNumber = generateCardNumber();
        card.setCardNumber(encryptCardNumber(cardNumber));
        card.setExpireDate(java.time.LocalDate.now().plusYears(3));
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(java.math.BigDecimal.ZERO);
        Card saved = cardRepository.save(card);
        return toRespDTO(saved);
    }

    @Override
    public void blockCard(Long cardId, User requester) {
        Card card = getCardEntityById(cardId, requester);
        if (!isAdminOrOwner(requester, card)) throw new AccessDeniedException(BadRequestError.NO_ACCESS.getMessage());
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    @Override
    public void activateCard(Long cardId, User requester) {
        Card card = getCardEntityById(cardId, requester);
        if (!isAdminOrOwner(requester, card)) throw new AccessDeniedException(BadRequestError.NO_ACCESS.getMessage());
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    @Override
    public void deleteCard(Long cardId, User requester) {
        Card card = getCardEntityById(cardId, requester);
        if (!isAdminOrOwner(requester, card)) throw new AccessDeniedException(BadRequestError.NO_ACCESS.getMessage());
        cardRepository.delete(card);
    }

    private void checkAndUpdateCardStatus(Card card) {
        if (card.getExpireDate() != null && card.getExpireDate().isBefore(java.time.LocalDate.now())) {
            if (card.getStatus() != CardStatus.EXPIRED) {
                card.setStatus(CardStatus.EXPIRED);
                cardRepository.save(card);
            }
        }
    }

    @Override
    public CardRespDTO getCardById(Long cardId, User requester) {
        Card card = getCardEntityById(cardId, requester);
        checkAndUpdateCardStatus(card);
        return toRespDTO(card);
    }

    @Override
    public List<CardRespDTO> getCardsByOwner(User owner) {
        List<Card> cards = cardRepository.findByOwner(owner);
        cards.forEach(this::checkAndUpdateCardStatus);
        return cards.stream().map(this::toRespDTO).collect(Collectors.toList());
    }

    @Override
    public Page<CardRespDTO> searchCards(User owner, String query, Pageable pageable) {
        Page<Card> page = cardRepository.searchUserCards(owner, query, pageable);
        page.forEach(this::checkAndUpdateCardStatus);
        return page.map(this::toRespDTO);
    }

    @Override
    public void transferBetweenCards(TransferReqDTO transferReqDTO, User requester) {
        Card from = getCardEntityById(transferReqDTO.getFromCardId(), requester);
        Card to = getCardEntityById(transferReqDTO.getToCardId(), requester);
        if (!from.getOwner().equals(requester) || !to.getOwner().equals(requester)) {
            throw new BadRequestException(BadRequestError.ONLY_OWN_CARDS_TRANSFER);
        }
        if (from.getStatus() == CardStatus.BLOCKED) {
            throw new BadRequestException(BadRequestError.FROM_CARD_BLOCKED);
        }
        if (to.getStatus() == CardStatus.BLOCKED) {
            throw new BadRequestException(BadRequestError.TO_CARD_BLOCKED);
        }
        if (from.getStatus() == CardStatus.EXPIRED) {
            throw new BadRequestException(BadRequestError.FROM_CARD_EXPIRED);
        }
        if (to.getStatus() == CardStatus.EXPIRED) {
            throw new BadRequestException(BadRequestError.TO_CARD_EXPIRED);
        }
        if (from.getBalance().compareTo(transferReqDTO.getAmount()) < 0) {
            throw new BadRequestException(BadRequestError.INSUFFICIENT_FUNDS);
        }
        from.setBalance(from.getBalance().subtract(transferReqDTO.getAmount()));
        to.setBalance(to.getBalance().add(transferReqDTO.getAmount()));
        cardRepository.save(from);
        cardRepository.save(to);
    }

    @Override
    public List<CardRespDTO> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        cards.forEach(this::checkAndUpdateCardStatus);
        return cards.stream().map(this::toRespDTO).collect(Collectors.toList());
    }

    @Override
    public CardBalanceRespDTO getCardBalance(Long cardId, User requester) {
        Card card = getCardEntityById(cardId, requester);
        checkAndUpdateCardStatus(card);
        CardBalanceRespDTO dto = new CardBalanceRespDTO();
        dto.setCardId(card.getId());
        dto.setBalance(card.getBalance());
        dto.setStatus(card.getStatus());
        return dto;
    }

    private Card getCardEntityById(Long cardId, User requester) {
        Optional<Card> cardOpt = cardRepository.findById(cardId);
        if (cardOpt.isEmpty()) throw new NotFoundException(NotFoundError.CARD_NOT_FOUND);
        Card card = cardOpt.get();
        if (!isAdminOrOwner(requester, card)) throw new BadRequestException(BadRequestError.NO_ACCESS);
        return card;
    }

    private CardRespDTO toRespDTO(Card card) {
        CardRespDTO dto = new CardRespDTO();
        dto.setId(card.getId());
        dto.setMaskedCardNumber(maskCardNumber(decryptCardNumber(card.getCardNumber())));
        dto.setOwnerEmail(card.getOwner().getEmail());
        dto.setExpireDate(card.getExpireDate());
        dto.setStatus(card.getStatus());
        dto.setBalance(card.getBalance());
        return dto;
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() < 4) return "****";
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }

    private String encryptCardNumber(String cardNumber) {
        return cardEncryptor.encrypt(cardNumber);
    }

    private String decryptCardNumber(String encryptedCardNumber) {
        return cardEncryptor.decrypt(encryptedCardNumber);
    }

    private boolean isAdminOrOwner(User requester, Card card) {
        return requester.getRoles().stream().anyMatch(r -> r.name().equals("ADMIN")) || card.getOwner().equals(requester);
    }

    private String generateCardNumber() {
        StringBuilder sb = new StringBuilder();
        sb.append('1');
        for (int i = 0; i < 14; i++) {
            sb.append((int)(Math.random() * 10));
        }
        String partial = sb.toString();
        int checksum = calculateLuhnChecksum(partial);
        sb.append(checksum);
        return sb.toString();
    }

    private int calculateLuhnChecksum(String number) {
        int sum = 0;
        boolean alternate = true;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = number.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        int mod = sum % 10;
        return mod == 0 ? 0 : 10 - mod;
    }
}
