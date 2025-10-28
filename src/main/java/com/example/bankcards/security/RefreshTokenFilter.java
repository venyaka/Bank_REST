package com.example.bankcards.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.bankcards.constant.PathConstants;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.errors.BadRequestError;
import com.example.bankcards.security.jwt.JwtUtils;
import com.example.bankcards.repository.UserRepository;

import java.io.IOException;

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
        if (request.getHeader("Refresh") != null) {
            String refreshToken = request.getHeader("Refresh");
            refreshToken = refreshToken.replace("Bearer", "");
            if (!jwtUtils.validateRefreshToken(refreshToken)) {
                throw new BadRequestException(BadRequestError.NOT_CORRECT_REFRESH_TOKEN);
            }

            String email = jwtUtils.getUserEmailFromToken(refreshToken);
            User user = userRepository.findByEmail(email).get();
            user.setRefreshToken(jwtUtils.generateRandomSequence());
            refreshToken = jwtUtils.generateRefreshToken(user);

            String accessToken = jwtUtils.generateToken(user);
            response.addHeader("Authorization", "Bearer " + accessToken);
            response.addHeader("Refresh", "Bearer " + refreshToken);

            userRepository.saveAndFlush(user);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Определяет, должен ли фильтр применяться к текущему запросу.
     * Фильтр активен только для эндпоинта обновления токена.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getRequestURI().equals(PathConstants.AUTHORIZE_CONTROLLER_PATH + "/refreshToken");
    }

}
