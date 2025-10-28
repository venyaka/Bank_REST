package com.example.bankcards.dto.request;

import com.example.bankcards.entity.Role;
import lombok.Data;

import java.util.Set;


@Data
public class UpdateUserReqDTO {

    @Pattern(regexp = "(?i)[a-zа-я]+", message = "Имя должно состоять из букв")
    private String firstName;

    @Pattern(regexp = "(?i)[a-zа-я]+", message = "Фамилия должна состоять из букв")
    private String lastName;

    private Set<Role> roles;
}