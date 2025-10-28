package com.example.bankcards.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO для запроса на обновление данных текущего аутентифицированного пользователя.
 */
@Data
public class UpdateCurrentUserReqDTO {
    /**
     * Новое имя пользователя. Должно состоять из букв.
     */
    @Pattern(regexp = "(?i)[a-zа-я]+", message = "Имя должно состоять из букв")
    private String firstName;

    /**
     * Новая фамилия пользователя. Должна состоять из букв.
     */
    @Pattern(regexp = "(?i)[a-zа-я]+", message = "Фамилия должна состоять из букв")
    private String lastName;
}
