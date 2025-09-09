package com.example.bankcards.controller.admin;

import com.example.bankcards.constant.PathConstants;
import com.example.bankcards.dto.request.CreateCardReqDTO;
import com.example.bankcards.dto.request.TransferReqDTO;
import com.example.bankcards.dto.response.CardRespDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.errors.NotFoundError;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(PathConstants.CARD_ADMIN_CONTROLLER_PATH)
@PreAuthorize("hasAuthority('ADMIN')")
public class CardAdminController {

    private final CardService cardService;

    private final UserService userService;

    private final UserRepository userRepository;


    @PostMapping()
    @Operation(summary = "Создать новую карту")
    public CardRespDTO createCard(@RequestBody CreateCardReqDTO createCardReqDTO) {
        return cardService.createCard(createCardReqDTO);
    }

    @PatchMapping("/{id}/block")
    @Operation(summary = "Блокировать карту")
    public void blockCard(@PathVariable Long id) {
        User user = getCurrentUser();
        cardService.blockCard(id, user);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Активировать карту")
    public void activateCard(@PathVariable Long id) {
        User user = getCurrentUser();
        cardService.activateCard(id, user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить карту")
    public void deleteCard(@PathVariable Long id) {
        User user = getCurrentUser();
        cardService.deleteCard(id, user);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все карты (только для администратора)")
    public List<CardRespDTO> getAllCards() {
        return cardService.getAllCards();
    }

    private User getCurrentUser() {
        String email = userService.getCurrentUserInfo().getEmail();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(NotFoundError.USER_NOT_FOUND));
    }
}
