package com.example.bankcards.security.jwt;

import com.example.bankcards.constant.PathConstants;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AuthorizeException;
import com.example.bankcards.exception.errors.AuthorizedError;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
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

import java.io.IOException;
import java.util.Arrays;

/**
 * Фильтр для обработки JWT токенов при каждом запросе.
 * <p>
 * Этот фильтр проверяет наличие access-токена в cookie. Если токен валиден,
 * он устанавливает аутентификацию в {@link SecurityContextHolder}.
 * <p>
 * Если access-токен истек, фильтр пытается обновить его с помощью refresh-токена,
 * также хранящегося в cookie. В случае успешного обновления, выпускается новая пара
 * токенов (access и refresh), и они устанавливаются в cookie ответа. Этот процесс
 * называется ротацией токенов.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    /**
     * Основной метод фильтра, выполняющий проверку и обновление токенов.
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getCookieValue(request, "accessToken");
        if (accessToken != null && !accessToken.isBlank()) {
            try {
                // 1. Попытка валидации access-токена
                jwtUtils.validateToken(accessToken);
                String email = jwtUtils.getUserEmailFromToken(accessToken);
                User user = (User) userService.loadUserByUsername(email);
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(email, null, user.getAuthorities()));
            } catch (AuthorizeException ex) {
                // 2. Если access-токен истек, пытаемся его обновить
                if (AuthorizedError.TOKEN_WAS_EXPIRED.name().equals(ex.getErrorName())) {
                    String refreshJwt = getCookieValue(request, "refreshToken");
                    if (refreshJwt != null) {
                        try {
                            // 3. Валидация refresh-токена
                            if (jwtUtils.validateRefreshToken(refreshJwt)) {
                                String email = jwtUtils.getUserEmailFromToken(refreshJwt);
                                User user = userRepository.findByEmail(email).orElse(null);
                                if (user != null) {
                                    // 4. Ротация токенов: выпуск новой пары access и refresh
                                    user.setRefreshToken(jwtUtils.generateRandomSequence());
                                    String newAccess = jwtUtils.generateToken(user);
                                    String newRefresh = jwtUtils.generateRefreshToken(user);
                                    userRepository.saveAndFlush(user);

                                    // 5. Установка новых токенов в HttpOnly cookie
                                    addHttpOnlyCookie(response, "accessToken", newAccess, 15 * 60); // 15 мин
                                    addHttpOnlyCookie(response, "refreshToken", newRefresh, 7 * 24 * 60 * 60); // 7 дней
                                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(email, null, user.getAuthorities()));
                                }
                            }
                        } catch (Exception ignore) {
                            // Игнорируем ошибки при обновлении, чтобы не прерывать запрос.
                            // Пользователь просто останется неаутентифицированным.
                        }
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Извлекает значение cookie по имени.
     *
     * @param request HTTP-запрос.
     * @param name    Имя cookie.
     * @return Значение cookie или null, если cookie не найдено.
     */
    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst().orElse(null);
    }

    /**
     * Добавляет в ответ безопасный HttpOnly cookie.
     *
     * @param response      HTTP-ответ.
     * @param name          Имя cookie.
     * @param value         Значение cookie.
     * @param maxAgeSeconds Время жизни cookie в секундах.
     */
    private void addHttpOnlyCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAgeSeconds);
        // cookie.setSecure(true); // ВАЖНО: Раскомментируйте эту строку в production при использовании HTTPS
        response.addCookie(cookie);
    }

    /**
     * Определяет, должен ли фильтр применяться к текущему запросу.
     * Фильтр не применяется к эндпоинтам авторизации и статическим ресурсам.
     */
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
