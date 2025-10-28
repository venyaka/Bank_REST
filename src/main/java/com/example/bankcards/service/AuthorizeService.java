package com.example.bankcards.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import com.example.bankcards.dto.request.RegisterReqDTO;
import com.example.bankcards.dto.request.UserAuthorizeReqDTO;
import com.example.bankcards.dto.response.TokenRespDTO;

/**
 * Сервис для аутентификации и регистрации пользователей.
 */
public interface AuthorizeService {

    /**
     * Авторизует пользователя и возвращает токены доступа и обновления.
     *
     * @param userAuthorizeDTO DTO с учетными данными пользователя (email и пароль).
     * @param response         Объект HttpServletResponse для установки cookie с токеном обновления.
     * @return ResponseEntity с DTO, содержащим токен доступа.
     */
    ResponseEntity<TokenRespDTO> authorizeUser(UserAuthorizeReqDTO userAuthorizeDTO, HttpServletResponse response);

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param registerDTO DTO с данными для регистрации.
     * @param request     Объект HttpServletRequest для получения URL приложения.
     */
    void registerUser(@Valid RegisterReqDTO registerDTO, HttpServletRequest request);

    /**
     * Отправляет код подтверждения на email пользователя.
     *
     * @param email   Email пользователя.
     * @param request Объект HttpServletRequest для получения URL приложения.
     */
    void sendVerificationCode(String email, HttpServletRequest request);

    /**
     * Верифицирует пользователя по email и токену.
     *
     * @param email             Email пользователя.
     * @param verificationToken Токен верификации.
     */
    void verificateUser(String email, String verificationToken);
}
