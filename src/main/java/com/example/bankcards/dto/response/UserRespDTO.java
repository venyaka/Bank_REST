package com.example.bankcards.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

/**
 * DTO для ответа с информацией о пользователе.
 */
@Data
public class UserRespDTO {

    /**
     * Уникальный идентификатор пользователя.
     */
    @NotNull
    private Long id;

    /**
     * Адрес электронной почты пользователя.
     */
    private String email;

    /**
     * Имя пользователя.
     */
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    private String lastName;

    /**
     * Набор ролей, назначенных пользователю.
     */
    private Set<String> roles;
}