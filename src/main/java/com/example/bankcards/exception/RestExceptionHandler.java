package com.example.bankcards.exception;

import com.example.bankcards.dto.response.BusinessExceptionRespDTO;
import com.example.bankcards.dto.response.ConstraintFailRespDTO;
import com.example.bankcards.dto.response.ValidationExceptionRespDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для REST контроллеров.
 * <p>
 * Перехватывает различные типы исключений и формирует стандартизированные
 * ответы об ошибках в формате JSON. Имеет высший приоритет выполнения
 * благодаря аннотации {@code @Order(Ordered.HIGHEST_PRECEDENCE)}.
 * </p>
 */
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Обрабатывает исключения типа {@link NotFoundException}.
     * Возвращает HTTP статус 404 Not Found.
     *
     * @param ex      Перехваченное исключение.
     * @param request HTTP запрос.
     * @return DTO с информацией об ошибке.
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public BusinessExceptionRespDTO handleExceptions(NotFoundException ex, HttpServletRequest request) {
        long httpStatusCode = 404L;
        return formBusinessExceptionDTO(httpStatusCode, ex.getErrorName(), ex.getMessage(), request.getRequestURI());
    }

    /**
     * Обрабатывает все остальные исключения, не перехваченные другими обработчиками.
     * Возвращает HTTP статус 500 Internal Server Error.
     * Включает в ответ часть стектрейса для отладки.
     *
     * @param ex      Перехваченное исключение.
     * @param request HTTP запрос.
     * @return DTO с информацией об ошибке.
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public BusinessExceptionRespDTO handleExceptions(Throwable ex, HttpServletRequest request) {
        long httpStatusCode = 500L;
        String stackTrace = Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .reduce("", (frstStr, scndStr) -> frstStr + "\n " + scndStr);
        stackTrace = stackTrace.length() > 200 ? stackTrace.substring(0, 200) : stackTrace;

        BusinessExceptionRespDTO businessExceptionRespDTO = formBusinessExceptionDTO(httpStatusCode, "INTERNAL_SERVER_ERROR",
                ex.getMessage(), request.getRequestURI());
        businessExceptionRespDTO.setDebugInfo(stackTrace);
        log.error("Internal server error: ", ex);
        return businessExceptionRespDTO;
    }

    /**
     * Обрабатывает исключения, связанные с некорректными именами свойств в запросах (например, при сортировке).
     * Возвращает HTTP статус 400 Bad Request.
     *
     * @param ex      Перехваченное исключение.
     * @param request HTTP запрос.
     * @return DTO с информацией об ошибке.
     */
    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public BusinessExceptionRespDTO handleExceptions(PropertyReferenceException ex, HttpServletRequest request) {
        long httpStatusCode = 400L;
        return formBusinessExceptionDTO(httpStatusCode, "BAD_REQUEST", ex.getMessage(), request.getRequestURI());
    }

    /**
     * Обрабатывает исключения нарушения ограничений валидации (например, {@code @Valid} на параметрах методов).
     * Возвращает HTTP статус 400 Bad Request.
     *
     * @param ex      Перехваченное исключение.
     * @param request HTTP запрос.
     * @return DTO с информацией об ошибке.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public BusinessExceptionRespDTO handleExceptions(ConstraintViolationException ex, HttpServletRequest request) {
        long httpStatusCode = 400L;
        BusinessExceptionRespDTO businessExceptionRespDTO = formBusinessExceptionDTO(httpStatusCode, "BAD_REQUEST", ex.getMessage(), request.getRequestURI());
        businessExceptionRespDTO.setDebugInfo(ex.getConstraintViolations().toString());

        return businessExceptionRespDTO;
    }

    /**
     * Переопределяет стандартный обработчик для {@link MethodArgumentNotValidException},
     * которое возникает при ошибках валидации DTO в теле запроса ({@code @RequestBody}).
     * Возвращает HTTP статус 400 Bad Request с детализированной информацией по каждой ошибке поля.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        long httpStatusCode = 400L;
        String debugInfo = ex.getMessage();
        ValidationExceptionRespDTO validationExceptionRespDTO = formValidationExceptionRespDTO(httpStatusCode, "BAD_REQUEST", validationErrorsFormat(ex.getFieldErrors()),
                ((ServletWebRequest) request).getRequest().getRequestURI(), ex.getFieldErrors());
        validationExceptionRespDTO.setDebugInfo(debugInfo);

        return new ResponseEntity<>(validationExceptionRespDTO, status);
    }

    /**
     * Обрабатывает исключения отказа в доступе (Spring Security).
     * Возвращает HTTP статус 403 Forbidden.
     *
     * @param ex      Перехваченное исключение.
     * @param request HTTP запрос.
     * @return DTO с информацией об ошибке.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public BusinessExceptionRespDTO handleExceptions(AccessDeniedException ex, HttpServletRequest request) {
        long httpStatusCode = 403L;
        String errorName = "FORBIDDEN";
        return formBusinessExceptionDTO(httpStatusCode, errorName, ex.getMessage(), request.getRequestURI());
    }

    /**
     * Обрабатывает исключения аутентификации (Spring Security).
     * Возвращает HTTP статус 401 Unauthorized.
     *
     * @param ex      Перехваченное исключение.
     * @param request HTTP запрос.
     * @return DTO с информацией об ошибке.
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public BusinessExceptionRespDTO handleExceptions(AuthenticationException ex, HttpServletRequest request) {
        long httpStatusCode = 401L;
        String errorName = "BAD_CREDENTIALS";
        return formBusinessExceptionDTO(httpStatusCode, errorName, ex.getMessage(), request.getRequestURI());
    }

    /**
     * Обрабатывает кастомные исключения {@link BadRequestException}.
     * Возвращает HTTP статус 400 Bad Request.
     *
     * @param ex      Перехваченное исключение.
     * @param request HTTP запрос.
     * @return DTO с информацией об ошибке.
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public BusinessExceptionRespDTO handleExceptions(BadRequestException ex, HttpServletRequest request) {
        long httpStatusCode = 400L;
        return formBusinessExceptionDTO(httpStatusCode, ex.getErrorName(), ex.getMessage(), request.getRequestURI());
    }

    /**
     * Обрабатывает кастомные исключения {@link AuthorizeException}.
     * Возвращает HTTP статус 401 Unauthorized.
     *
     * @param ex      Перехваченное исключение.
     * @param request HTTP запрос.
     * @return DTO с информацией об ошибке.
     */
    @ExceptionHandler(AuthorizeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public BusinessExceptionRespDTO handleExceptions(AuthorizeException ex, HttpServletRequest request) {
        long httpStatusCode = 401L;
        return formBusinessExceptionDTO(httpStatusCode, ex.getErrorName(), ex.getMessage(), request.getRequestURI());
    }

    /**
     * Вспомогательный метод для формирования DTO с общей информацией об ошибке.
     */
    private BusinessExceptionRespDTO formBusinessExceptionDTO(Long status, String errorName, String message, String path) {
        BusinessExceptionRespDTO businessExceptionRespDTO = new BusinessExceptionRespDTO();
        businessExceptionRespDTO.setError(errorName);
        businessExceptionRespDTO.setMessage(message);
        businessExceptionRespDTO.setPath(path);
        businessExceptionRespDTO.setTimestamp(LocalDateTime.now());
        businessExceptionRespDTO.setStatus(status);
        return businessExceptionRespDTO;
    }

    /**
     * Вспомогательный метод для форматирования списка ошибок валидации полей в одну строку.
     */
    private String validationErrorsFormat(List<FieldError> fieldErrorList) {
        return fieldErrorList.stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
    }

    /**
     * Вспомогательный метод для формирования DTO с детализированной информацией об ошибках валидации.
     */
    private ValidationExceptionRespDTO formValidationExceptionRespDTO(Long status, String errorName, String message, String path, List<FieldError> fieldErrorList) {
        ValidationExceptionRespDTO validationExceptionRespDTO = new ValidationExceptionRespDTO();
        validationExceptionRespDTO.setError(errorName);
        validationExceptionRespDTO.setMessage(message);
        validationExceptionRespDTO.setPath(path);
        validationExceptionRespDTO.setTimestamp(LocalDateTime.now());
        validationExceptionRespDTO.setStatus(status);
        List<ConstraintFailRespDTO> constraintFailRespDTOList = fieldErrorList.stream().map(fieldError -> {
            ConstraintFailRespDTO constraintFailRespDTO = new ConstraintFailRespDTO();
            constraintFailRespDTO.setFieldName(fieldError.getField());
            constraintFailRespDTO.setRejectedValue(fieldError.getRejectedValue());
            constraintFailRespDTO.setMessage(fieldError.getDefaultMessage());
            constraintFailRespDTO.setCode(fieldError.getCode());
            return constraintFailRespDTO;
        }).collect(Collectors.toList());
        validationExceptionRespDTO.setConstraintFailRespDTOList(constraintFailRespDTOList);
        return validationExceptionRespDTO;
    }
}
