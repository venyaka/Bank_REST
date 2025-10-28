package com.example.bankcards.controller;

import com.example.bankcards.constant.PathConstants;
import com.example.bankcards.dto.response.CardBlockRequestRespDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardBlockRequestService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.errors.NotFoundError;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(PathConstants.CARD_CONTROLLER_PATH)
public class CardBlockRequestController {

    private final CardBlockRequestService blockRequestService;

    private final UserService userService;

    private final UserRepository userRepository;


    @PostMapping("/{id}/block-request")
    @Operation(summary = "Запросить блокировку своей карты")
    public CardBlockRequestRespDTO requestBlockCard(@PathVariable Long id) {
        User user = getCurrentUser();
        return blockRequestService.createBlockRequest(id, user);
    }

    @GetMapping("/block-requests")
    @Operation(summary = "Посмотреть свои запросы на блокировку карт")
    public List<CardBlockRequestRespDTO> getMyBlockRequests() {
        User user = getCurrentUser();
        return blockRequestService.getUserBlockRequests(user);
    }

    private User getCurrentUser() {
        String email = userService.getCurrentUserInfo().getEmail();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(NotFoundError.USER_NOT_FOUND));
    }
}

