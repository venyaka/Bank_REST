package com.example.bankcards.entity;

import org.springframework.security.core.GrantedAuthority;

/**
 * Представляет роли, которые может иметь пользователь.
 */
public enum Role implements GrantedAuthority {

    /**
     * Стандартная роль пользователя.
     */
    USER,
    /**
     * Роль администратора с повышенными привилегиями.
     */
    ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
