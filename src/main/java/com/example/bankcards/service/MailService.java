package com.example.bankcards.service;

import jakarta.servlet.http.HttpServletRequest;
import com.example.bankcards.entity.User;

/**
 * Сервис для отправки электронных писем.
 * <p>
 * Предоставляет методы для отправки писем для верификации пользователя и восстановления пароля.
 * </p>
 */
public interface MailService {

    /**
     * Отправляет письмо для верификации пользователя.
     *
     * @param user    Пользователь, которому отправляется письмо.
     * @param request Объект HttpServletRequest для получения URL приложения.
     */
    void sendUserVerificationMail(User user, HttpServletRequest request);

    /**
     * Отправляет письмо для восстановления пароля.
     *
     * @param user    Пользователь, которому отправляется письмо.
     * @param request Объект HttpServletRequest для получения URL приложения.
     */
    void sendPasswordRestoreMail(User user, HttpServletRequest request);
}
