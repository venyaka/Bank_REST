package com.example.bankcards.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserAuthorizeReqDTO {

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank
    private String password;
}
