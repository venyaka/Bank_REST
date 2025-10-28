package com.example.bankcards.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.bankcards.dto.response.BusinessExceptionRespDTO;
import com.example.bankcards.exception.AuthorizeException;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.RestExceptionHandler;

import java.io.IOException;

/**
 * Фильтр для перехвата исключений, возникающих в цепочке фильтров Spring Security.
 * <p>
 * Этот фильтр является одним из первых в цепочке и его основная задача —
 * обернуть выполнение последующих фильтров в блок try-catch. Если в каком-либо
 * из фильтров (например, в {@link JwtTokenFilter}) возникнет исключение,
 * этот фильтр перехватит его и делегирует обработку глобальному обработчику
 * {@link RestExceptionHandler}.
 * <p>
 * Это необходимо, так как стандартный {@code @ControllerAdvice} перехватывает
 * исключения только на уровне контроллеров, но не на уровне фильтров. Без этого
 * фильтра клиент получил бы стандартную HTML-страницу ошибки вместо
 * корректного JSON-ответа.
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private static final String ENCODE = "UTF-8";

    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

    private final RestExceptionHandler exceptionHandler;

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding(ENCODE);
        response.setContentType(CONTENT_TYPE);
        try {
            filterChain.doFilter(request, response);
        } catch (NotFoundException e) {
            BusinessExceptionRespDTO responseBody = exceptionHandler.handleExceptions(e, request, response);
            response.setStatus(404);
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));

        } catch (AccessDeniedException e) {
            BusinessExceptionRespDTO responseBody = exceptionHandler.handleExceptions(e, request, response);
            response.setStatus(403);
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } catch (AuthenticationException e) {
            BusinessExceptionRespDTO responseBody = exceptionHandler.handleExceptions(e, request, response);
            response.setStatus(401);
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } catch (AuthorizeException e) {
            BusinessExceptionRespDTO responseBody = exceptionHandler.handleExceptions(e, request, response);
            response.setStatus(401);
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } catch (BadRequestException e) {
            BusinessExceptionRespDTO responseBody = exceptionHandler.handleExceptions(e, request, response);
            response.setStatus(400);
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } catch (Throwable throwable) {
            BusinessExceptionRespDTO responseBody = exceptionHandler.handleExceptions(throwable, request, response);
            response.setStatus(500);
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        }

    }

}
