package com.example.bankcards.dto.response;

import lombok.Data;

/**
 * DTO для ответа с токенами доступа и обновления.
 */
@Data
public class TokenRespDTO {

    /**
     * Токен доступа (Access Token) для аутентификации запросов.
     */
    private String accessToken;

    /**
     * Токен обновления (Refresh Token) для получения новой пары токенов.
     */
    private String refreshToken;

}
