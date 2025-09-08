package com.example.bankcards.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.bankcards.constant.PathConstants;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AuthorizeException;
import com.example.bankcards.exception.errors.AuthorizedError;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getCookieValue(request, "accessToken");
        if (accessToken != null && !accessToken.isBlank()) {
            try {
                jwtUtils.validateToken(accessToken);
                String email = jwtUtils.getUserEmailFromToken(accessToken);
                User user = (User) userService.loadUserByUsername(email);
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(email, null, user.getAuthorities()));
            } catch (AuthorizeException ex) {

                if (AuthorizedError.TOKEN_WAS_EXPIRED.name().equals(ex.getErrorName())) {
                    String refreshJwt = getCookieValue(request, "refreshToken");
                    if (refreshJwt != null) {
                        try {
                            if (jwtUtils.validateRefreshToken(refreshJwt)) {
                                String email = jwtUtils.getUserEmailFromToken(refreshJwt);
                                User user = userRepository.findByEmail(email).orElse(null);
                                if (user != null) {
                                    // Ротация refresh и выпуск нового access
                                    user.setRefreshToken(jwtUtils.generateRandomSequence());
                                    String newAccess = jwtUtils.generateToken(user);
                                    String newRefresh = jwtUtils.generateRefreshToken(user);
                                    userRepository.saveAndFlush(user);
                                    // Установить новые cookie
                                    addHttpOnlyCookie(response, "accessToken", newAccess, 15 * 60); // 15 мин
                                    addHttpOnlyCookie(response, "refreshToken", newRefresh, 7 * 24 * 60 * 60); // 7 дней
                                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(email, null, user.getAuthorities()));
                                }
                            }
                        } catch (Exception ignore) {

                        }
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst().orElse(null);
    }

    private void addHttpOnlyCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(value).append(";")
          .append(" Path=/;")
          .append(" HttpOnly;")
          .append(" Max-Age=").append(maxAgeSeconds).append(";")
          .append(" SameSite=Lax");
        response.addHeader("Set-Cookie", sb.toString());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith(PathConstants.AUTHORIZE_CONTROLLER_PATH)
                || uri.startsWith("/swagger")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/favicon.ico")
                || uri.startsWith("/css")
                || uri.startsWith("/js")
                || uri.startsWith("/images")
                || uri.startsWith("/static");
    }
}
