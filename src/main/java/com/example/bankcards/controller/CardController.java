package com.example.bankcards.controller;

import com.example.bankcards.constant.PathConstants;
import com.example.bankcards.dto.request.CreateCardReqDTO;
import com.example.bankcards.dto.request.TransferReqDTO;
import com.example.bankcards.dto.response.CardBalanceRespDTO;
import com.example.bankcards.dto.response.CardRespDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.errors.NotFoundError;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(PathConstants.CARD_CONTROLLER_PATH)
public class CardController {

    private final CardService cardService;

    private final UserService userService;

    private final UserRepository userRepository;


    @GetMapping("/{id}")
    @Operation(summary = "Получить карту по id")
    public CardRespDTO getCard(@PathVariable Long id) {
        User user = getCurrentUser();
        return cardService.getCardById(id, user);
    }

    @GetMapping
    @Operation(summary = "Получить все свои карты")
    public List<CardRespDTO> getMyCards() {
        User user = getCurrentUser();
        return cardService.getCardsByOwner(user);
    }

    @PostMapping("/transfer")
    @Operation(summary = "Перевод между своими картами")
    public void transfer(@Valid @RequestBody TransferReqDTO transferReqDTO) {
        User user = getCurrentUser();
        cardService.transferBetweenCards(transferReqDTO, user);
    }

    @GetMapping("/{id}/balance")
    @Operation(summary = "Получить баланс карты по id")
    public CardBalanceRespDTO getCardBalance(@PathVariable Long id) {
        User user = getCurrentUser();
        return cardService.getCardBalance(id, user);
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск и пагинация своих карт")
    public ResponseEntity<Page<CardRespDTO>> searchUserCards(@RequestParam(required = false) String query,
                                                            Pageable pageable) {
        User user = getCurrentUser();
        return ResponseEntity.ok(cardService.searchCards(user, query, pageable));
    }

    private User getCurrentUser() {
        String email = userService.getCurrentUserInfo().getEmail();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(NotFoundError.USER_NOT_FOUND));
    }
}
