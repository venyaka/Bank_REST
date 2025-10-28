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

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(PathConstants.USER_CONTROLLER_PATH)
public class UserController {

    private final UserService userService;
    private final CookieService cookieService;

    @GetMapping("/info")
    @Operation(summary = "Получение информации о текущем авторизированном пользователе")
    public UserRespDTO getUserInfo() {
        return userService.getCurrentUserInfo();
    }

    @PatchMapping("/update")
    @Operation(summary = "Обновление текущего авторизированного пользователя")
    public UserRespDTO updateCurrentUser(
            @ModelAttribute UpdateCurrentUserReqDTO updateCurrentUserReqDTO) {
        return userService.updateCurrentUser(updateCurrentUserReqDTO);
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход пользователя из системы")
    public void logout(HttpServletResponse response) {
        userService.logout();
        cookieService.clearAuthCookies(response);
    }
}
