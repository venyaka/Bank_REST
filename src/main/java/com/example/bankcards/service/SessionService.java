package com.example.bankcards.service;

import com.example.bankcards.entity.UserSession;

/**
 * Сервис для управления сессиями пользователей.
 */
public interface SessionService {

    /**
     * Сохраняет новую сессию для пользователя.
     *
     * @param userId ID пользователя, для которого создается сессия.
     * @return Созданный объект UserSession.
     */
    UserSession saveNewSession(Long userId);

}
