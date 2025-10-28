package com.example.bankcards.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO для запроса на аутентификацию (вход) пользователя.
 */
@Data
public class UserAuthorizeReqDTO {

    /**
     * Адрес электронной почты пользователя.
     */
    @NotBlank(message = "Email is required")
    @Email
    private String email;

    /**
     * Пароль пользователя.
     */
    @NotBlank
    private String password;
}
