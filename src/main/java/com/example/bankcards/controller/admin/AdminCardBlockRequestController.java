package com.example.bankcards.controller.admin;

import com.example.bankcards.constant.PathConstants;
import com.example.bankcards.dto.response.CardBlockRequestRespDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardBlockRequestService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(PathConstants.BLOCK_REQUEST_CONTROLLER_PATH)
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminCardBlockRequestController {

    private final CardBlockRequestService blockRequestService;

    private final UserService userService;

    private final UserRepository userRepository;


    @GetMapping
    @Operation(summary = "Получить все запросы на блокировку карт")
    public List<CardBlockRequestRespDTO> getAllBlockRequests() {
        return blockRequestService.getAllBlockRequests();
    }

    @PostMapping("/{requestId}/approve")
    @Operation(summary = "Подтвердить запрос на блокировку карты (карта будет заблокирована)")
    public CardBlockRequestRespDTO approveBlockRequest(@PathVariable Long requestId, @RequestParam String comment) {
        User admin = getCurrentUser();
        return blockRequestService.approveBlockRequest(requestId, admin, comment);
    }

    @PostMapping("/{requestId}/reject")
    @Operation(summary = "Отклонить запрос на блокировку карты")
    public CardBlockRequestRespDTO rejectBlockRequest(@PathVariable Long requestId, @RequestParam String comment) {
        User admin = getCurrentUser();
        return blockRequestService.rejectBlockRequest(requestId, admin, comment);
    }

    private User getCurrentUser() {
        String email = userService.getCurrentUserInfo().getEmail();
        return userRepository.findByEmail(email).orElseThrow();
    }
}

