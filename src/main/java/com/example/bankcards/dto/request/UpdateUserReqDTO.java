package com.example.bankcards.dto.request;

import com.example.bankcards.entity.Role;
import lombok.Data;

import java.util.Set;


@Data
public class UpdateUserReqDTO {

    private String firstName;

    private String lastName;

    private Set<Role> roles;
}