package com.example.bankcards.dto.request;

import com.example.bankcards.entity.Role;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Set;

/**
 * DTO для запроса на обновление данных пользователя администратором.
 */
@Data
public class UpdateUserReqDTO {

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

    /**
     * Новый набор ролей пользователя.
     */
    private Set<Role> roles;
}