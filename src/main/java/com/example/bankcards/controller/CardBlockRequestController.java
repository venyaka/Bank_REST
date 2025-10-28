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

/**
 * Контроллер для управления запросами на блокировку карт текущего пользователя.
 * <p>
 * Предоставляет эндпоинты для создания запроса на блокировку карты и получения
 * списка ранее созданных запросов текущим пользователем. Бизнес-логику обработки
 * делегирует в {@link CardBlockRequestService}. Для получения информации о текущем
 * пользователе используются {@link UserService} и {@link UserRepository}.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(PathConstants.CARD_CONTROLLER_PATH)
public class CardBlockRequestController {

    private final CardBlockRequestService blockRequestService;

    private final UserService userService;

    private final UserRepository userRepository;


    /**
     * Создать запрос на блокировку своей карты.
     * <p>
     * Получает текущего аутентифицированного пользователя, проверяет наличие карты с указанным id
     * и создаёт запись запроса на блокировку через {@link CardBlockRequestService}.
     *
     * @param id идентификатор карты, для которой запрашивается блокировка.
     * @return DTO с информацией о созданном запросе на блокировку ({@link CardBlockRequestRespDTO}).
     */
    @PostMapping("/{id}/block-request")
    @Operation(summary = "Запросить блокировку своей карты")
    public CardBlockRequestRespDTO requestBlockCard(@PathVariable Long id) {
        User user = getCurrentUser();
        return blockRequestService.createBlockRequest(id, user);
    }

    /**
     * Получить список своих запросов на блокировку карт.
     * <p>
     * Возвращает коллекцию DTO с информацией о запросах, связанных с текущим пользователем.
     *
     * @return список запросов на блокировку карт текущего пользователя.
     */
    @GetMapping("/block-requests")
    @Operation(summary = "Посмотреть свои запросы на блокировку карт")
    public List<CardBlockRequestRespDTO> getMyBlockRequests() {
        User user = getCurrentUser();
        return blockRequestService.getUserBlockRequests(user);
    }

    /**
     * Получить сущность пользователя, соответствующую текущей аутентифицированной сессии.
     * <p>
     * Извлекает email через {@link UserService#getCurrentUserInfo()} и ищет пользователя в репозитории.
     * Если пользователь не найден, выбрасывается {@link NotFoundException} с кодом {@link NotFoundError#USER_NOT_FOUND}.
     *
     * @return текущий {@link User}
     * @throws NotFoundException если пользователь не найден в репозитории
     */
    private User getCurrentUser() {
        String email = userService.getCurrentUserInfo().getEmail();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(NotFoundError.USER_NOT_FOUND));
    }
}
