package com.example.bankcards.service.impl;

import com.example.bankcards.service.CookieService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

/**
 * Реализация сервиса для работы с cookie.
 */
@Service
public class CookieServiceImpl implements CookieService {

    @Value("${jwt.token.access-expiration-seconds}")
    private int accessExpirationSeconds;

    @Value("${jwt.token-refresh.refresh-expiration-seconds}")
    private int refreshExpirationSeconds;

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
//                .secure(true) // Передавать только по HTTPS
                .path("/")
                .sameSite("Strict")
                .maxAge(accessExpirationSeconds) // 15 мин
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
//                .secure(true) // Передавать только по HTTPS
                .path("/") // Ограничить путь для refresh-токена
                .sameSite("Strict") // Более строгая политика
                .maxAge(refreshExpirationSeconds) // 7 дней
                .build();
        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAuthCookies(HttpServletResponse response) {
        ResponseCookie access = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
//                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        ResponseCookie refresh = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
//                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", access.toString());
        response.addHeader("Set-Cookie", refresh.toString());
    }
}
