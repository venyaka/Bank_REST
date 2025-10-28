package com.example.bankcards.util;

import jakarta.servlet.http.HttpServletRequest;

public class UrlPathUtility {

    private UrlPathUtility() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    public static String getSiteUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }
}
