package com.example.bankcards.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;


@Data
public class UserRespDTO {

    @NotBlank
    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private Set<String> roles;
}