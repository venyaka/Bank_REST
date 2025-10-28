package com.example.bankcards.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.bankcards.constant.PathConstants;
import com.example.bankcards.dto.request.UpdateCurrentUserReqDTO;
import com.example.bankcards.dto.response.UserRespDTO;
import com.example.bankcards.service.CookieService;
import com.example.bankcards.service.UserService;

/**
 * Контроллер для операций, связанных с текущим пользователем.
 * <p>
 * Предоставляет эндпоинты для получения информации о текущем авторизованном пользователе,
 * обновления его данных и выхода (очистки аутентификационных cookie).
 * Вся бизнес-логика делегируется в {@link UserService}, работа с cookie — в {@link CookieService}.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(PathConstants.USER_CONTROLLER_PATH)
public class UserController {

    private final UserService userService;
    private final CookieService cookieService;

    /**
     * Получить информацию о текущем авторизованном пользователе.
     * <p>
     * Возвращает DTO с основными полями пользователя (email, имя и т.п.), полученными из контекста безопасности.
     *
     * @return {@link UserRespDTO} с информацией о текущем пользователе
     */
    @GetMapping("/info")
    @Operation(summary = "Получение информации о текущем авторизированном пользователе")
    public UserRespDTO getUserInfo() {
        return userService.getCurrentUserInfo();
    }

    /**
     * Обновить данные текущего пользователя.
     * <p>
     * Принимает изменения через {@link UpdateCurrentUserReqDTO} (например, имя, аватар и т.п.) в форме multipart/form-data
     * благодаря использованию {@link org.springframework.web.bind.annotation.ModelAttribute}. Сервис {@link UserService}
     * выполняет валидацию и сохранение изменений.
     *
     * @param updateCurrentUserReqDTO DTO с полями для обновления текущего пользователя
     * @return обновлённый {@link UserRespDTO}
     */
    @PatchMapping("/update")
    @Operation(summary = "Обновление текущего авторизированного пользователя")
    public UserRespDTO updateCurrentUser(
            @ModelAttribute UpdateCurrentUserReqDTO updateCurrentUserReqDTO) {
        return userService.updateCurrentUser(updateCurrentUserReqDTO);
    }

    /**
     * Выйти из системы (logout).
     * <p>
     * Вызывает разлогин в {@link UserService} (например, очистка сессии/токенов на сервере) и очищает
     * клиентские аутентификационные cookie через {@link CookieService#clearAuthCookies(HttpServletResponse)}.
     *
     * @param response {@link HttpServletResponse} — используется для удаления/обнуления cookie в ответе
     */
    @PostMapping("/logout")
    @Operation(summary = "Выход пользователя из системы")
    public void logout(HttpServletResponse response) {
        userService.logout();
        cookieService.clearAuthCookies(response);
    }
}
