package com.example.bankcards.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import com.example.bankcards.dto.request.UpdateCurrentUserReqDTO;
import com.example.bankcards.dto.response.UserRespDTO;
import com.example.bankcards.entity.User;

public interface UserService extends UserDetailsService {

    UserRespDTO getCurrentUserInfo();

    UserRespDTO updateCurrentUser(UpdateCurrentUserReqDTO updateCurrentUserReqDTO);

    UserRespDTO getUserById(Long id);

    UserRespDTO getUserByEmail(String email);

    UserRespDTO getResponseDTO(User user);

    void logout();}
