package com.example.bankcards.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

/**
 * DTO для запроса на создание нового пользователя администратором.
 */
@Data
public class CreateUserReqDTO {

    /**
     * Адрес электронной почты пользователя. Должен быть уникальным.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Имя пользователя.
     */
    @NotBlank(message = "First name is required")
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    @NotBlank(message = "Last name is required")
    private String lastName;

    /**
     * Пароль пользователя.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 24, message = "Password must be between 8 and 24 characters")
    private String password;

    /**
     * Набор ролей, назначаемых пользователю.
     */
    private Set<String> roles;
}