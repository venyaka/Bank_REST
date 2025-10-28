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
import org.springframework.http.ResponseEntity;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.errors.NotFoundError;

import java.util.List;

/**
 * Админский контроллер для управления запросами на блокировку карт.
 * <p>
 * Предоставляет API для просмотра всех запросов на блокировку карт и действий администратора
 * по подтверждению или отклонению этих запросов. Доступ к контроллеру ограничен ролью ADMIN
 * (аннотация {@link PreAuthorize}). Бизнес-логику обработки запросов делегирует в
 * {@link CardBlockRequestService}. Для получения информации о текущем администраторе используется
 * {@link UserService} и {@link UserRepository}.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(PathConstants.BLOCK_REQUEST_CONTROLLER_PATH)
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminCardBlockRequestController {

    private final CardBlockRequestService blockRequestService;

    private final UserService userService;

    private final UserRepository userRepository;


    /**
     * Получить все запросы на блокировку карт.
     * <p>
     * Возвращает список DTO со всеми существующими запросами на блокировку — полезно для отображения
     * в админской панели и фильтрации/дальнейшей обработки.
     *
     * @return список {@link CardBlockRequestRespDTO} со всеми запросами на блокировку карт
     */
    @GetMapping
    @Operation(summary = "Получить все запросы на блокировку карт")
    public List<CardBlockRequestRespDTO> getAllBlockRequests() {
        return blockRequestService.getAllBlockRequests();
    }

    /**
     * Подтвердить запрос на блокировку карты.
     * <p>
     * Администратор подтверждает указанный запрос (по идентификатору), в результате карта будет заблокирована.
     * Параметр {@code comment} является необязательным и может быть использован для логов/уведомлений.
     * Метод возвращает {@link ResponseEntity} с DTO и HTTP-статусом.
     *
     * @param requestId идентификатор запроса на блокировку
     * @param comment   (необязательный) комментарий администратора к решению
     * @return {@link ResponseEntity} с телом {@link CardBlockRequestRespDTO} и статусом 200 OK
     */
    @PostMapping("/{requestId}/approve")
    @Operation(summary = "Подтвердить запрос на блокировку карты (карта будет заблокирована)")
    public ResponseEntity<CardBlockRequestRespDTO> approveBlockRequest(@PathVariable Long requestId,
                                                                        @RequestParam(required = false) String comment) {
        User admin = getCurrentUser();
        CardBlockRequestRespDTO resp = blockRequestService.approveBlockRequest(requestId, admin, comment);
        return ResponseEntity.ok(resp);
    }

    /**
     * Отклонить запрос на блокировку карты.
     * <p>
     * Администратор отклоняет указанный запрос на блокировку. Комментарий администратора
     * сохраняется и может быть отправлен пользователю или использован в логах. Параметр
     * {@code comment} необязателен.
     *
     * @param requestId идентификатор запроса на блокировку
     * @param comment   (необязательный) комментарий администратора с объяснением причины отклонения
     * @return {@link ResponseEntity} с телом {@link CardBlockRequestRespDTO} и статусом 200 OK
     */
    @PostMapping("/{requestId}/reject")
    @Operation(summary = "Отклонить запрос на блокировку карты")
    public ResponseEntity<CardBlockRequestRespDTO> rejectBlockRequest(@PathVariable Long requestId,
                                                                       @RequestParam(required = false) String comment) {
        User admin = getCurrentUser();
        CardBlockRequestRespDTO resp = blockRequestService.rejectBlockRequest(requestId, admin, comment);
        return ResponseEntity.ok(resp);
    }

    /**
     * Получить сущность текущего аутентифицированного администратора.
     * <p>
     * Извлекает email текущего пользователя через {@link UserService#getCurrentUserInfo()} и затем
     * загружает {@link User} из {@link UserRepository}. В случае отсутствия пользователя будет
     * выброшено соответствующее исключение из {@code Optional#orElseThrow()}.
     *
     * @return текущий {@link User} (администратор)
     */
    private User getCurrentUser() {
        String email = userService.getCurrentUserInfo().getEmail();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(NotFoundError.USER_NOT_FOUND));
    }
}
