package com.example.bankcards.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Фильтр для добавления контекстной информации в MDC (Mapped Diagnostic Context).
 * <p>
 * Добавляет в каждый лог-запись:
 * - requestId — уникальный идентификатор запроса для трассировки
 * - userId — ID текущего пользователя (если аутентифицирован)
 * - userEmail — email текущего пользователя
 * - clientIp — IP-адрес клиента
 * - requestUri — URI запроса
 * - httpMethod — HTTP-метод запроса
 * <p>
 * Это позволяет связать все логи одного запроса в ELK/Kibana.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class MdcLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";
    private static final String USER_EMAIL = "userEmail";
    private static final String CLIENT_IP = "clientIp";
    private static final String REQUEST_URI = "requestUri";
    private static final String HTTP_METHOD = "httpMethod";

    private static final String X_REQUEST_ID_HEADER = "X-Request-ID";
    private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        try {
            // Устанавливаем MDC-контекст
            setupMdc(request);

            // Добавляем requestId в заголовок ответа для клиента
            response.setHeader(X_REQUEST_ID_HEADER, MDC.get(REQUEST_ID));

            log.debug("Входящий запрос: {} {}", request.getMethod(), request.getRequestURI());

            filterChain.doFilter(request, response);

        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Запрос завершён: {} {} - {} за {} мс",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);

            // Очищаем MDC после завершения запроса
            clearMdc();
        }
    }

    /**
     * Устанавливает значения в MDC для текущего запроса.
     */
    private void setupMdc(HttpServletRequest request) {
        // Request ID - берём из заголовка или генерируем новый
        String requestId = request.getHeader(X_REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString().substring(0, 8);
        }
        MDC.put(REQUEST_ID, requestId);

        // Информация о запросе
        MDC.put(REQUEST_URI, request.getRequestURI());
        MDC.put(HTTP_METHOD, request.getMethod());
        MDC.put(CLIENT_IP, getClientIp(request));

        // Информация о пользователе (если аутентифицирован)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            MDC.put(USER_EMAIL, auth.getName());

            // Если principal содержит User entity с ID
            if (auth.getPrincipal() instanceof com.example.bankcards.entity.User user) {
                MDC.put(USER_ID, String.valueOf(user.getId()));
            }
        }
    }

    /**
     * Очищает все значения MDC.
     */
    private void clearMdc() {
        MDC.remove(REQUEST_ID);
        MDC.remove(USER_ID);
        MDC.remove(USER_EMAIL);
        MDC.remove(CLIENT_IP);
        MDC.remove(REQUEST_URI);
        MDC.remove(HTTP_METHOD);
    }

    /**
     * Определяет реальный IP-адрес клиента с учётом прокси.
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader(X_FORWARDED_FOR_HEADER);
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            // Берём первый IP из списка (реальный клиент)
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Исключаем статические ресурсы и healthcheck
        return path.startsWith("/static/") ||
               path.startsWith("/actuator/health") ||
               path.startsWith("/favicon.ico");
    }
}

