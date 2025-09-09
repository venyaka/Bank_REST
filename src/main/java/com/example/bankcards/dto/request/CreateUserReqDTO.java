package com.example.bankcards.dto.request;

import lombok.Data;

import java.util.Set;


@Data
public class CreateUserReqDTO {

    private String email;

    private String firstName;

    private String lastName;

    private String password;

    private Set<String> roles;
}