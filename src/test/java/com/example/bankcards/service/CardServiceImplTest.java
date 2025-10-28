package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateCardReqDTO;
import com.example.bankcards.dto.request.TransferReqDTO;
import com.example.bankcards.dto.response.CardRespDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.CardServiceImpl;
import com.example.bankcards.util.CardEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CardServiceImplTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CardEncryptor cardEncryptor;

    @InjectMocks
    private CardServiceImpl cardService;

    private User user;
    private User admin;
    private Card card;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRoles(Set.of(Role.USER));

        card = new Card();
        card.setId(10L);
        card.setOwner(user);
        card.setCardNumber("encrypted");
        card.setExpireDate(LocalDate.now().plusYears(1));
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.valueOf(1000));

        admin = new User();
        admin.setId(1L);
        admin.setEmail("test@example.com");
        admin.setRoles(Set.of(Role.ADMIN));
    }

    @Test
    @DisplayName("Успешное создание карты")
    void createCard_success() {
        CreateCardReqDTO req = new CreateCardReqDTO();
        req.setOwnerId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardEncryptor.encrypt(anyString())).thenReturn("encrypted");
        when(cardEncryptor.decrypt(anyString())).thenReturn("1234567812345678");

        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card cardToSave = invocation.getArgument(0);
            cardToSave.setId(15L);
            return cardToSave;
        });

        CardRespDTO resp = cardService.createCard(req);

        assertNotNull(resp.getId());
        assertEquals(15L, resp.getId());
        assertEquals(user.getEmail(), resp.getOwnerEmail());
        assertEquals(CardStatus.ACTIVE, resp.getStatus());
    }

    @Test
    @DisplayName("Создание карты для несуществующего пользователя")
    void createCard_userNotFound() {
        CreateCardReqDTO req = new CreateCardReqDTO();
        req.setOwnerId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cardService.createCard(req));
    }

    @Test
    @DisplayName("Успешный перевод между картами")
    void transferBetweenCards_success() {
        User owner = user;
        Card from = new Card();
        from.setId(1L);
        from.setOwner(owner);
        from.setStatus(CardStatus.ACTIVE);
        from.setBalance(BigDecimal.valueOf(500));

        Card to = new Card();
        to.setId(2L);
        to.setOwner(owner);
        to.setStatus(CardStatus.ACTIVE);
        to.setBalance(BigDecimal.valueOf(100));

        TransferReqDTO req = new TransferReqDTO();
        req.setFromCardId(1L);
        req.setToCardId(2L);
        req.setAmount(BigDecimal.valueOf(200));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        cardService.transferBetweenCards(req, owner);

        assertEquals(0, BigDecimal.valueOf(300).compareTo(from.getBalance()));
        assertEquals(0, BigDecimal.valueOf(300).compareTo(to.getBalance()));
    }

    @Test
    @DisplayName("Перевод между картами при недостаточном балансе")
    void transferBetweenCards_insufficientFunds() {
        User owner = user;
        Card from = new Card();
        from.setId(1L);
        from.setOwner(owner);
        from.setStatus(CardStatus.ACTIVE);
        from.setBalance(BigDecimal.valueOf(100));

        Card to = new Card();
        to.setId(2L);
        to.setOwner(owner);
        to.setStatus(CardStatus.ACTIVE);
        to.setBalance(BigDecimal.valueOf(100));

        TransferReqDTO req = new TransferReqDTO();
        req.setFromCardId(1L);
        req.setToCardId(2L);
        req.setAmount(BigDecimal.valueOf(200));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(BadRequestException.class, () -> cardService.transferBetweenCards(req, owner));
    }

    @Test
    @DisplayName("Успешное обновление баланса карты администратором")
    void updateCardBalance_success() {
        User admin = new User();
        admin.setId(2L);
        admin.setRoles(Set.of(Role.ADMIN));

        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
        cardService.updateCardBalance(10L, BigDecimal.valueOf(555), admin);
        assertEquals(0, BigDecimal.valueOf(555).compareTo(card.getBalance()));
    }

    @Test
    @DisplayName("Обновление баланса для несуществующей карты администратором")
    void updateCardBalance_cardNotFound() {

        when(cardRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cardService.updateCardBalance(99L, BigDecimal.TEN, admin));
    }

    @Test
    @DisplayName("Успешная блокировка карты")
    void blockCard_success() {
        card.setStatus(CardStatus.ACTIVE);
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
        cardService.blockCard(10L, user);
        assertEquals(CardStatus.BLOCKED, card.getStatus());
    }

    @Test
    @DisplayName("Успешная активация карты")
    void activateCard_success() {
        card.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
        cardService.activateCard(10L, user);
        assertEquals(CardStatus.ACTIVE, card.getStatus());
    }

    @Test
    @DisplayName("Успешное получение карты по ID")
    void getCardById_success() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
        when(cardEncryptor.decrypt(anyString())).thenReturn("1234567812345678");
        var resp = cardService.getCardById(10L, user);
        assertEquals(card.getId(), resp.getId());
    }

    @Test
    @DisplayName("Получение несуществующей карты по ID")
    void getCardById_cardNotFound() {
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cardService.getCardById(99L, user));
    }
}

