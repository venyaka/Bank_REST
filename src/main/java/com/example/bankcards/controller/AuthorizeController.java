package com.example.bankcards.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.bankcards.constant.PathConstants;
import com.example.bankcards.dto.request.RegisterReqDTO;
import com.example.bankcards.dto.request.UserAuthorizeReqDTO;
import com.example.bankcards.dto.response.TokenRespDTO;
import com.example.bankcards.service.AuthorizeService;

/**
 * Контроллер для операций авторизации и регистрации пользователей.
 * <p>
 * Обрабатывает вход (login), регистрацию (register), повторную отправку верификационного кода
 * и подтверждение регистрации по email/token. Бизнес-логику реальной аутентификации/регистрации
 * делегирует в {@link AuthorizeService}.
 * <p>
 * Все эндпоинты находятся под общим префиксом, определённым в {@link PathConstants#AUTHORIZE_CONTROLLER_PATH}.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(PathConstants.AUTHORIZE_CONTROLLER_PATH)
public class AuthorizeController {

    private final AuthorizeService authorizeService;

    /**
     * Эндпоинт для авторизации пользователя.
     * <p>
     * Принимает email и password в теле запроса (в DTO {@link UserAuthorizeReqDTO}).
     * В случае успешной авторизации возвращает тело с access/refresh токенами ({@link TokenRespDTO})
     * и устанавливает соответствующие HttpOnly cookie через объект {@link HttpServletResponse}.
     * Всё остальное (проверка пароля, генерация токенов, установка cookie) реализовано в {@link AuthorizeService}.
     *
     * @param userAuthorizeDTO DTO с полями для авторизации (email, password). Должен проходить валидацию.
     * @param response        HttpServletResponse, используется для установки HttpOnly cookie (access/refresh токены).
     * @return {@link ResponseEntity} с телом {@link TokenRespDTO}, содержащим access и refresh токены.
     */
    @PostMapping("/login")
    @Operation(summary = "Эндпоинт для авторизации: принимает email и password и устанавливает HttpOnly cookies с access/refresh токенами")
    public ResponseEntity<TokenRespDTO> authorizeUser(@Valid @RequestBody UserAuthorizeReqDTO userAuthorizeDTO, HttpServletResponse response) {
        return authorizeService.authorizeUser(userAuthorizeDTO, response);
    }

    /**
     * Регистрация нового пользователя.
     * <p>
     * Принимает данные регистрации в {@link RegisterReqDTO}, создаёт запись пользователя и
     * инициирует процесс подтверждения email (отправка верификационного кода/ссылки).
     * Фактическая отправка письма и создание кода выполняются в {@link AuthorizeService}.
     *
     * @param registerDTO данные для регистрации (например: email, password, имя и т.п.). Должны проходить валидацию.
     * @param request     HttpServletRequest — может использоваться сервисом для построения ссылок в письме (host, scheme и т.д.).
     */
    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя. После регистрации требуется подтверждение email")
    public void registerUser(@Valid @RequestBody RegisterReqDTO registerDTO, HttpServletRequest request) {
        authorizeService.registerUser(registerDTO, request);
    }

    /**
     * Повторная отправка верификационного кода на указанный email.
     * <p>
     * Используется, если пользователь не получил или потерял первоначальный код подтверждения.
     * Сервис {@link AuthorizeService} выполнит генерацию/восстановление кода и отправку письма.
     *
     * @param email   email пользователя, на который необходимо выслать код подтверждения.
     * @param request HttpServletRequest — может использоваться для формирования ссылок в письме.
     */
    @PostMapping("/verificateCode")
    @Operation(summary = "Повторная отправка верификационного кода на почту")
    public void sendVerificationCode(@RequestParam String email, HttpServletRequest request) {
        authorizeService.sendVerificationCode(email, request);
    }

    /**
     * Верификация зарегистрированного пользователя.
     * <p>
     * Принимает email и токен/код верификации в параметрах запроса и производит подтверждение аккаунта.
     * Метод поддерживает как GET, так и POST для удобства (переход из письма — GET, повторная отправка и т.п. — POST).
     *
     * @param email email пользователя, который нужно верифицировать.
     * @param token токен/код верификации, ранее отправленный пользователю по email.
     */
    @RequestMapping(value = "/verification", method = { RequestMethod.GET, RequestMethod.POST })
    @Operation(summary = "Верификация зарегистрированного пользователя по почте и токену")
    public void verificateUser(@RequestParam String email,
                               @RequestParam(name = "token") String token) {
        authorizeService.verificateUser(email, token);
    }
}
