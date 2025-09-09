package com.example.bankcards.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public interface CookieService {

    void addAuthCookies(HttpServletResponse response, String accessToken, String refreshToken);

    void clearAuthCookies(HttpServletResponse response);
}

