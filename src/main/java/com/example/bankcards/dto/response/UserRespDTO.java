package com.example.bankcards.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;


@Data
public class UserRespDTO {

    @NotBlank
    @NotNull
    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private Set<String> roles;
}