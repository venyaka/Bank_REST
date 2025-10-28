package com.example.bankcards.util;

import jakarta.servlet.http.HttpServletRequest;

public class UrlPathUtility {
/**
 * Утилитарный класс для работы с URL-адресами в HTTP-запросах.
 */

    /**
     * Приватный конструктор для предотвращения инстанцирования.
     */
    private UrlPathUtility() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Извлекает базовый URL сайта (схема, хост, порт) из HTTP-запроса.
     * Например, из "http://example.com/api/users" вернет "http://example.com".
     *
     * @param request Входящий HTTP-запрос.
     * @return Строка, представляющая базовый URL сайта.
     */
    public static String getSiteUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }
}
