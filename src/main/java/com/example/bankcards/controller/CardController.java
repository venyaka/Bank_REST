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

/**
 * Контроллер для управления банковскими картами пользователя.
 * <p>
 * Предоставляет REST-эндпоинты для получения информации о картах текущего пользователя,
 * получения баланса, поиска/пагинации карт и перевода средств между своими картами.
 * Вся бизнес-логика делегируется в {@link CardService}, а информация о текущем пользователе
 * получается через {@link UserService} и {@link UserRepository}.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(PathConstants.CARD_CONTROLLER_PATH)
public class CardController {

    private final CardService cardService;

    private final UserService userService;

    private final UserRepository userRepository;


    /**
     * Получить карту по её идентификатору.
     * <p>
     * Возвращает DTO с подробной информацией о карте, если она принадлежит текущему пользователю.
     * Валидацию прав доступа и проверку существования карты выполняет {@link CardService}.
     *
     * @param id идентификатор карты
     * @return {@link CardRespDTO} с информацией о карте
     * @throws com.example.bankcards.exception.NotFoundException если карта не найдена или не принадлежит пользователю
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получить карту по id")
    @PreAuthorize("hasRole('ADMIN') or @cardRepository.findById(#id).get().getOwner().getEmail() == authentication.name")
    public CardRespDTO getCard(@PathVariable Long id) {
        User user = getCurrentUser();
        return cardService.getCardById(id, user);
    }

    /**
     * Получить все карты текущего пользователя.
     *
     * @return список {@link CardRespDTO} — все карты, принадлежащие текущему пользователю
     */
    @GetMapping
    @Operation(summary = "Получить все свои карты")
    public List<CardRespDTO> getMyCards() {
        User user = getCurrentUser();
        return cardService.getCardsByOwner(user);
    }

    /**
     * Выполнить перевод между картами текущего пользователя.
     * <p>
     * Принимает в теле запроса {@link TransferReqDTO} с данными перевода (id карт, сумма и т.п.).
     * Все проверки (наличие средств, принадлежность карт пользователю, валидность сумм) выполняются в {@link CardService}.
     *
     * @param transferReqDTO DTO с параметрами перевода. Должен быть валидирован (аннотация {@link Valid}).
     */
    @PostMapping("/transfer")
    @Operation(summary = "Перевод между своими картами")
    public void transfer(@Valid @RequestBody TransferReqDTO transferReqDTO) {
        User user = getCurrentUser();
        cardService.transferBetweenCards(transferReqDTO, user);
    }

    /**
     * Получить баланс конкретной карты текущего пользователя.
     *
     * @param id идентификатор карты
     * @return {@link CardBalanceRespDTO} с информацией о балансе карты
     * @throws com.example.bankcards.exception.NotFoundException если карта не найдена или не принадлежит пользователю
     */
    @GetMapping("/{id}/balance")
    @Operation(summary = "Получить баланс карты по id")
    @PreAuthorize("hasRole('ADMIN') or @cardRepository.findById(#id).get().getOwner().getEmail() == authentication.name")
    public CardBalanceRespDTO getCardBalance(@PathVariable Long id) {
        User user = getCurrentUser();
        return cardService.getCardBalance(id, user);
    }

    /**
     * Поиск и пагинация карт текущего пользователя.
     * <p>
     * Поддерживает необязательный параметр query для фильтрации по номеру/маске/комментарию и
     * объект {@link Pageable} для управления размером страницы и сортировкой.
     *
     * @param query    необязательная строка поиска
     * @param pageable параметры пагинации и сортировки
     * @return {@link ResponseEntity} со страницей {@link CardRespDTO}
     */
    @GetMapping("/search")
    @Operation(summary = "Поиск и пагинация своих карт")
    public ResponseEntity<Page<CardRespDTO>> searchUserCards(@RequestParam(required = false) String query,
                                                            Pageable pageable) {
        User user = getCurrentUser();
        return ResponseEntity.ok(cardService.searchCards(user, query, pageable));
    }

    /**
     * Получить сущность пользователя, соответствующую текущей аутентифицированной сессии.
     * <p>
     * Извлекает email текущего пользователя через {@link UserService#getCurrentUserInfo()} и затем
     * загружает {@link User} из {@link UserRepository}. В случае отсутствия выдаёт {@link NotFoundException}.
     *
     * @return {@link User} текущего пользователя
     * @throws NotFoundException если пользователь не найден
     */
    private User getCurrentUser() {
        String email = userService.getCurrentUserInfo().getEmail();
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(NotFoundError.USER_NOT_FOUND));
    }
}
