package com.example.bankcards.security.jwt;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AuthorizeException;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.errors.AuthorizedError;
import com.example.bankcards.exception.errors.BadRequestError;
import com.example.bankcards.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

/**
 * Утилитарный класс для работы с JWT (JSON Web Tokens).
 * <p>
 * Отвечает за генерацию, валидацию и извлечение данных из access и refresh токенов.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.token.time-expiration}")
    private Long jwtExpirationTime;

    @Value("${jwt.token-refresh.time-expiration}")
    private Long jwtRefreshExpirationTime;

    private final UserRepository userRepository;

    /**
     * Генерирует access-токен для указанного пользователя.
     *
     * @param user Пользователь, для которого создается токен.
     * @return Строка с access-токеном.
     */
    public String generateToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("typeToken", "access");

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .setIssuedAt(new Date())
                .setNotBefore(new Date())
                .signWith(getSigningKey()).compact();
    }

    /**
     * Генерирует refresh-токен для указанного пользователя.
     * Включает в себя уникальную последовательность из поля {@code user.refreshToken}.
     *
     * @param user Пользователь, для которого создается токен.
     * @return Строка с refresh-токеном.
     */
    public String generateRefreshToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("refreshToken", user.getRefreshToken());
        claims.put("typeToken", "refresh");

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationTime))
                .setIssuedAt(new Date())
                .setNotBefore(new Date())
                .signWith(getSigningKey()).compact();
    }

    /**
     * Проверяет валидность refresh-токена.
     * <p>
     * Проверка включает:
     * 1. Стандартную валидацию JWT (подпись, срок действия).
     * 2. Проверку существования пользователя с таким email.
     * 3. Сравнение уникальной последовательности из токена с той, что хранится в БД.
     * Это защищает от использования старых refresh-токенов после их ротации.
     * </p>
     *
     * @param token Refresh-токен для проверки.
     * @return {@code true}, если токен полностью валиден.
     * @throws BadRequestException если токен не прошел проверку.
     */
    public boolean validateRefreshToken(String token) {
        try {
            validateToken(token);
        } catch (AuthorizeException exception) {
            throw new BadRequestException(BadRequestError.NOT_CORRECT_REFRESH_TOKEN.getMessage() + " " + exception.getMessage(), BadRequestError.NOT_CORRECT_REFRESH_TOKEN.name());
        }

        Claims claims = getAllClaimsFromToken(token);
        String email = claims.getSubject();
        String givenRefreshToken = (String) claims.get("refreshToken");

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new BadRequestException(BadRequestError.NOT_CORRECT_REFRESH_TOKEN);
        }

        User user = optionalUser.get();
        String correctRefreshToken = user.getRefreshToken();

        return correctRefreshToken.equals(givenRefreshToken);
    }

    /**
     * Проверяет JWT токен на валидность (подпись, срок действия).
     *
     * @param token Токен для проверки.
     * @return {@code true}, если токен валиден.
     * @throws AuthorizeException если токен некорректен или истек.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException | SignatureException |
                 DecodingException ex) {
            throw new AuthorizeException(AuthorizedError.NOT_CORRECT_TOKEN);
        } catch (ExpiredJwtException ex) {
            throw new AuthorizeException(AuthorizedError.TOKEN_WAS_EXPIRED);
        }
    }

    /**
     * Извлекает email пользователя (subject) из токена.
     *
     * @param token JWT токен.
     * @return Email пользователя.
     */
    public String getUserEmailFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    /**
     * Извлекает уникальную последовательность refresh-токена из его payload.
     *
     * @param token JWT refresh-токен.
     * @return Уникальная строка.
     */
    public String getRefreshStringFromToken(String token) {
        return (String) getAllClaimsFromToken(token).get("refreshToken");
    }

    /**
     * Извлекает тип токена (access/refresh) из его payload.
     *
     * @param token JWT токен.
     * @return Тип токена.
     */
    public String getTypeTokenFromToken(String token) {
        return (String) getAllClaimsFromToken(token).get("typeToken");
    }

    /**
     * Генерирует случайную алфавитно-цифровую последовательность.
     * Используется для создания уникального идентификатора refresh-токена.
     *
     * @return Случайная строка.
     */
    public String generateRandomSequence() {
        return RandomStringUtils.randomAlphanumeric(50);
    }

    /**
     * Извлекает все claims (полезную нагрузку) из токена.
     * Этот приватный метод используется для оптимизации, чтобы избежать многократного парсинга токена.
     *
     * @param token JWT токен.
     * @return Объект {@link Claims}.
     */
    private Claims getAllClaimsFromToken(String token) {
        // В новых версиях библиотеки jjwt парсинг с истекшим сроком действия вызывает ExpiredJwtException.
        // Чтобы получить claims из истекшего токена (для процесса обновления),
        // нужно перехватить исключение и извлечь claims из него.
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * Создает и возвращает ключ для подписи JWT.
     *
     * @return Объект {@link Key}.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
