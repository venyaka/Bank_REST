package com.example.bankcards.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.bankcards.constant.PathConstants;
import com.example.bankcards.dto.request.RegisterReqDTO;
import com.example.bankcards.dto.request.UserAuthorizeReqDTO;
import com.example.bankcards.dto.response.TokenRespDTO;
import com.example.bankcards.service.AuthorizeService;

@RestController
@RequiredArgsConstructor
@RequestMapping(PathConstants.AUTHORIZE_CONTROLLER_PATH)
public class AuthorizeController {

    private final AuthorizeService authorizeService;


    @PostMapping("/login")
    @Operation(summary = "Эндпоинт для авторизации: принимает email и password и устанавливает HttpOnly cookies с access/refresh токенами")
    public ResponseEntity<TokenRespDTO> authorizeUser(@Valid @RequestBody UserAuthorizeReqDTO userAuthorizeDTO, HttpServletResponse response) {
        return authorizeService.authorizeUser(userAuthorizeDTO, response);
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя. После регистрации требуется подтверждение email")
    public void registerUser(@Valid @RequestBody RegisterReqDTO registerDTO, HttpServletRequest request) {
        authorizeService.registerUser(registerDTO, request);
    }

    @PostMapping("/verificateCode")
    @Operation(summary = "Повторная отправка верификационного кода на почту")
    public void sendVerificationCode(@RequestParam String email, HttpServletRequest request) {
        authorizeService.sendVerificationCode(email, request);
    }

    @RequestMapping(value = "/verification", method = { RequestMethod.GET, RequestMethod.POST })
    @Operation(summary = "Верификация зарегистрированного пользователя по почте и токену")
    public void verificateUser(@RequestParam String email,
                               @RequestParam(name = "token") String token) {
        authorizeService.verificateUser(email, token);
    }
}
