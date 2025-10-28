package com.example.bankcards.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с cookie.
 * <p>
 * Предоставляет методы для добавления и удаления cookie,
 * связанных с аутентификацией пользователя.
 * </p>
 */
@Service
public interface CookieService {

    /**
     * Добавляет cookie с токенами доступа и обновления в HTTP-ответ.
     *
     * @param response     Объект HttpServletResponse, в который будут добавлены cookie.
     * @param accessToken  Токен доступа.
     * @param refreshToken Токен обновления.
     */
    void addAuthCookies(HttpServletResponse response, String accessToken, String refreshToken);

    /**
     * Удаляет cookie аутентификации из HTTP-ответа.
     *
     * @param response Объект HttpServletResponse, из которого будут удалены cookie.
     */
    void clearAuthCookies(HttpServletResponse response);
}
