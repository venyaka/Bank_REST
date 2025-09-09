package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateUserReqDTO;
import com.example.bankcards.dto.request.UpdateUserReqDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.example.bankcards.dto.request.UpdateCurrentUserReqDTO;
import com.example.bankcards.dto.response.UserRespDTO;
import com.example.bankcards.entity.User;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserRespDTO getCurrentUserInfo();

    UserRespDTO updateCurrentUser(UpdateCurrentUserReqDTO updateCurrentUserReqDTO);

    UserRespDTO getUserById(Long id);

    UserRespDTO getUserByEmail(String email);

    UserRespDTO getResponseDTO(User user);

    void logout();

    List<UserRespDTO> getAllUsers();

    UserRespDTO createUser(CreateUserReqDTO createUserReqDTO);

    UserRespDTO updateUser(Long id, UpdateUserReqDTO updateUserReqDTO);

    void deleteUser(Long id);
}
