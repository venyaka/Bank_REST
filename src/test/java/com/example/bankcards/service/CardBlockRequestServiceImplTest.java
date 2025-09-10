package com.example.bankcards.service;

import com.example.bankcards.dto.response.CardBlockRequestRespDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.impl.CardBlockRequestServiceImpl;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardBlockRequestServiceImplTest {
    @Mock
    private CardBlockRequestRepository blockRequestRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardService cardService;
    @Mock
    private CardEncryptor cardEncryptor;

    @InjectMocks
    private CardBlockRequestServiceImpl service;

    private User user;
    private User admin;
    private Card card;
    private CardBlockRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setRoles(Set.of(Role.USER));
        admin = new User();
        admin.setId(2L);
        admin.setEmail("admin@example.com");
        admin.setRoles(Set.of(Role.ADMIN));
        card = new Card();
        card.setId(10L);
        card.setOwner(user);
        request = new CardBlockRequest();
        request.setId(100L);
        request.setCard(card);
        request.setUser(user);
        request.setStatus(CardBlockRequest.Status.PENDING);
        request.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createBlockRequest_success() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
        when(blockRequestRepository.findByUser(user)).thenReturn(List.of());
        when(blockRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        CardBlockRequestRespDTO dto = service.createBlockRequest(10L, user);
        assertEquals(CardBlockRequest.Status.PENDING, dto.getStatus());
        assertEquals(user.getId(), dto.getUserId());
        assertEquals(card.getId(), dto.getCardId());
    }

    @Test
    void createBlockRequest_cardNotFound() {
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.createBlockRequest(99L, user));
    }

    @Test
    void createBlockRequest_noAccess() {
        Card otherCard = new Card();
        otherCard.setId(11L);
        otherCard.setOwner(admin);
        when(cardRepository.findById(11L)).thenReturn(Optional.of(otherCard));
        assertThrows(BadRequestException.class, () -> service.createBlockRequest(11L, user));
    }

    @Test
    void createBlockRequest_alreadyRequested() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
        CardBlockRequest existing = new CardBlockRequest();
        existing.setCard(card);
        existing.setUser(user);
        existing.setStatus(CardBlockRequest.Status.PENDING);
        when(blockRequestRepository.findByUser(user)).thenReturn(List.of(existing));
        assertThrows(BadRequestException.class, () -> service.createBlockRequest(10L, user));
    }

    @Test
    void approveBlockRequest_success() {
        when(blockRequestRepository.findById(100L)).thenReturn(Optional.of(request));
        when(blockRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        CardBlockRequestRespDTO dto = service.approveBlockRequest(100L, admin, "ok");
        assertEquals(CardBlockRequest.Status.APPROVED, dto.getStatus());
        assertEquals(admin.getId(), dto.getAdminId());
        verify(cardService).blockCard(card.getId(), admin);
    }

    @Test
    void approveBlockRequest_notAdmin() {
        when(blockRequestRepository.findById(100L)).thenReturn(Optional.of(request));
        assertThrows(BadRequestException.class, () -> service.approveBlockRequest(100L, user, "fail"));
    }

    @Test
    void approveBlockRequest_alreadyProcessed() {
        request.setStatus(CardBlockRequest.Status.APPROVED);
        when(blockRequestRepository.findById(100L)).thenReturn(Optional.of(request));
        assertThrows(BadRequestException.class, () -> service.approveBlockRequest(100L, admin, "fail"));
    }

    @Test
    void rejectBlockRequest_success() {
        when(blockRequestRepository.findById(100L)).thenReturn(Optional.of(request));
        when(blockRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        CardBlockRequestRespDTO dto = service.rejectBlockRequest(100L, admin, "no");
        assertEquals(CardBlockRequest.Status.REJECTED, dto.getStatus());
        assertEquals(admin.getId(), dto.getAdminId());
    }

    @Test
    void rejectBlockRequest_notAdmin() {
        when(blockRequestRepository.findById(100L)).thenReturn(Optional.of(request));
        assertThrows(BadRequestException.class, () -> service.rejectBlockRequest(100L, user, "fail"));
    }

    @Test
    void rejectBlockRequest_alreadyProcessed() {
        request.setStatus(CardBlockRequest.Status.REJECTED);
        when(blockRequestRepository.findById(100L)).thenReturn(Optional.of(request));
        assertThrows(BadRequestException.class, () -> service.rejectBlockRequest(100L, admin, "fail"));
    }
}

