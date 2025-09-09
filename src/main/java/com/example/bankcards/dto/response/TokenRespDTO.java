package com.example.bankcards.dto.response;

import lombok.Data;

@Data
public class TokenRespDTO {

    private String accessToken;

    private String refreshToken;

}
