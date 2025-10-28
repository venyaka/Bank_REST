package com.example.bankcards.security;

import com.example.bankcards.constant.PathConstants;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.errors.BadRequestError;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Фильтр, отвечающий за обновление (ротацию) JWT токенов.
 * <p>
 * Этот фильтр активируется только для эндпоинта {@code /authorize/refreshToken}.
 * Его задача — принять refresh-токен из заголовка "Refresh", проверить его валидность,
 * сгенерировать новую пару access и refresh токенов и вернуть их в заголовках ответа.
 * </p>
 * <p>
 * Процесс ротации включает в себя обновление уникальной последовательности в refresh-токене,
 * что делает предыдущий refresh-токен недействительным и повышает безопасность.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    /**
     * Основной метод фильтра для обработки запроса на обновление токена.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Refresh");
        final String tokenPrefix = "Bearer ";

        // Проверяем наличие заголовка и префикса "Bearer "
        if (authHeader != null && authHeader.startsWith(tokenPrefix)) {
            String refreshToken = authHeader.substring(tokenPrefix.length());

            // Валидируем refresh-токен. Метод validateRefreshToken выбросит исключение, если токен невалиден.
            if (!jwtUtils.validateRefreshToken(refreshToken)) {
                throw new BadRequestException(BadRequestError.NOT_CORRECT_REFRESH_TOKEN);
            }

            String email = jwtUtils.getUserEmailFromToken(refreshToken);
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                // Генерируем новую случайную последовательность для ротации refresh-токена
                user.setRefreshToken(jwtUtils.generateRandomSequence());

                // Создаем новую пару токенов
                String newAccessToken = jwtUtils.generateToken(user);
                String newRefreshToken = jwtUtils.generateRefreshToken(user);

                // Добавляем новые токены в заголовки ответа
                response.addHeader("Authorization", tokenPrefix + newAccessToken);
                response.addHeader("Refresh", tokenPrefix + newRefreshToken);

                // Сохраняем обновленного пользователя с новой последовательностью refresh-токена
                userRepository.saveAndFlush(user);
            } else {
                // Эта ветка маловероятна, так как validateRefreshToken уже проверяет наличие пользователя,
                // но является дополнительной мерой безопасности.
                throw new BadRequestException(BadRequestError.NOT_CORRECT_REFRESH_TOKEN);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Определяет, должен ли фильтр применяться к текущему запросу.
     * Фильтр активен только для эндпоинта обновления токена.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().equals(PathConstants.AUTHORIZE_CONTROLLER_PATH + "/refreshToken");
    }

}
