package com.example.bankcards.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * DTO для ответа от сервиса геолокации IpStack.
 * Игнорирует все неизвестные поля при десериализации JSON.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpStackResponse {

    /**
     * Город, определенный по IP-адресу.
     */
    private String city;
}
