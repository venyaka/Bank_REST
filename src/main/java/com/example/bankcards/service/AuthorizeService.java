package com.example.bankcards.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import com.example.bankcards.dto.request.RegisterReqDTO;
import com.example.bankcards.dto.request.UserAuthorizeReqDTO;
import com.example.bankcards.dto.response.TokenRespDTO;

public interface AuthorizeService {

    ResponseEntity<TokenRespDTO> authorizeUser(UserAuthorizeReqDTO userAuthorizeDTO);

    void registerUser(@Valid RegisterReqDTO registerDTO, HttpServletRequest request);

    void sendVerificationCode(String email, HttpServletRequest request);

    void verificateUser(String email, String verificationToken);
}
