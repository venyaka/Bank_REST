package com.example.bankcards.controller.admin;

import com.example.bankcards.constant.PathConstants;
import com.example.bankcards.dto.request.CreateCardReqDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для операций с банковскими картами, доступный только администраторам.
 * <p>
 * Предоставляет эндпоинты для создания, блокировки, активации, удаления карт, получения списка
 * всех карт, а также тестовый эндпоинт для изменения баланса карты.
 * <p>
 * Все методы этого контроллера защищены и требуют наличия у пользователя прав администратора ('ADMIN').
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(PathConstants.CARD_ADMIN_CONTROLLER_PATH)
@PreAuthorize("hasAuthority('ADMIN')")
public class CardAdminController {

    private final CardService cardService;
    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * Создает новую банковскую карту на основе предоставленных данных.
     *
     * @param createCardReqDTO DTO с данными для создания карты. Подвергается валидации.
     * @return DTO с информацией о созданной карте.
     */
    @PostMapping
    @Operation(summary = "Создать новую карту")
    public CardRespDTO createCard(@Valid @RequestBody CreateCardReqDTO createCardReqDTO) {
        return cardService.createCard(createCardReqDTO);
    }

    /**
     * Блокирует банковскую карту по ее идентификатору.
     *
     * @param id Идентификатор карты, которую необходимо заблокировать.
     */
    @PatchMapping("/{id}/block")
    @Operation(summary = "Блокировать карту")
    public void blockCard(@PathVariable Long id) {
        User user = getCurrentUser();
        cardService.blockCard(id, user);
    }

    /**
     * Активирует заблокированную банковскую карту по ее идентификатору.
     *
     * @param id Идентификатор карты, которую необходимо активировать.
     */
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Активировать карту")
    public void activateCard(@PathVariable Long id) {
        User user = getCurrentUser();
        cardService.activateCard(id, user);
    }

    /**
     * Удаляет банковскую карту по ее идентификатору.
     *
     * @param id Идентификатор карты для удаления.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить карту")
    public void deleteCard(@PathVariable Long id) {
        User user = getCurrentUser();
        cardService.deleteCard(id, user);
    }

    /**
     * Возвращает список всех банковских карт, зарегистрированных в системе.
     *
     * @return Список DTO с информацией о всех картах.
     */
    @GetMapping
    @Operation(summary = "Получить все карты (только для администратора)")
    public List<CardRespDTO> getAllCards() {
        return cardService.getAllCards();
    }

    /**
     * Обновляет баланс карты. Endpoint предназначен исключительно для тестирования.
     * <p>
     *
     * @param id      Идентификатор карты для обновления баланса.
     * @param balance Новое значение баланса.
     * @return {@link ResponseEntity} со статусом 204 No Content в случае успеха.
     */
    @PatchMapping("/{id}/test-balance")
    @Operation(summary = "Изменить баланс карты (только для тестирования)", description = "Тестовый endpoint. Не использовать в проде!")
    public ResponseEntity<Void> updateCardBalanceForTest(@PathVariable Long id, @RequestParam("balance") java.math.BigDecimal balance) {
        User user = getCurrentUser();
        cardService.updateCardBalance(id, balance, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Вспомогательный метод для получения текущего аутентифицированного пользователя.
     *
     * @return Объект {@link User}, представляющий текущего пользователя.
     * @throws NotFoundException если пользователь не найден в репозитории.
     */
    private User getCurrentUser() {
        String email = userService.getCurrentUserInfo().getEmail();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(NotFoundError.USER_NOT_FOUND));
    }
}
