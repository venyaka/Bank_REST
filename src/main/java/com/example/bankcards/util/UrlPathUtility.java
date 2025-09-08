package com.example.bankcards.util;

import jakarta.servlet.http.HttpServletRequest;

public class UrlPathUtility {

    public static String getSiteUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }
}
