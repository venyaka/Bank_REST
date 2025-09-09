package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCardReqDTO {

    @NotNull
    private Long ownerId;
}

