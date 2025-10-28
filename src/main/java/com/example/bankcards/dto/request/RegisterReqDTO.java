package com.example.bankcards.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для запроса на регистрацию нового пользователя.
 */
@Data
public class RegisterReqDTO {

    /**
     * Имя пользователя. Должно состоять из букв.
     */
    @NotBlank
    @Pattern(regexp = "(?i)[a-zа-я]+", message = "Имя должно состоять из букв")
    private String firstName;

    /**
     * Фамилия пользователя. Должна состоять из букв.
     */
    @NotBlank
    @Pattern(regexp = "(?i)[a-zа-я]+", message = "Фамилия должна состоять из букв")
    private String lastName;

    /**
     * Пароль. Длина от 8 до 24 символов.
     */
    @NotBlank
    @Size(min = 8, max = 24, message = "Пароль должен быть больше 8 символов, но меньше 24")
    private String password;

    /**
     * Адрес электронной почты.
     */
    @NotBlank(message = "Email is required")
    @Email
    private String email;
}
