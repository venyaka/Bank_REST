package com.example.bankcards.service.impl;


import com.example.bankcards.dto.request.CreateUserReqDTO;
import com.example.bankcards.dto.request.UpdateUserReqDTO;
import com.example.bankcards.security.jwt.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.bankcards.dto.request.UpdateCurrentUserReqDTO;
import com.example.bankcards.dto.response.UserRespDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AuthorizeException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.errors.AuthorizedError;
import com.example.bankcards.exception.errors.NotFoundError;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException(NotFoundError.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public UserRespDTO getCurrentUserInfo() {
        User user = this.getCurrentUser();
        return getResponseDTO(user);
    }

    @Override
    @Transactional
    public UserRespDTO updateCurrentUser(UpdateCurrentUserReqDTO updateCurrentUserReqDTO) {
        User user = this.getCurrentUser();

        if (updateCurrentUserReqDTO.getFirstName() != null && !updateCurrentUserReqDTO.getFirstName().isBlank()) {
            user.setFirstName(updateCurrentUserReqDTO.getFirstName());
        }
        if (updateCurrentUserReqDTO.getLastName() != null && !updateCurrentUserReqDTO.getLastName().isBlank()) {
            user.setLastName(updateCurrentUserReqDTO.getLastName());
        }
        userRepository.save(user);

        return getResponseDTO(user);
    }


    @Override
    @Transactional
    public UserRespDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundError.USER_NOT_FOUND));
        return getResponseDTO(user);
    }


    @Transactional
    @Override
    public UserRespDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(NotFoundError.USER_NOT_FOUND));
        return getResponseDTO(user);
    }

    @Override
    public UserRespDTO getResponseDTO(User user) {
        UserRespDTO userRespDTO = new UserRespDTO();
        userRespDTO.setId(user.getId());
        userRespDTO.setEmail(user.getEmail());
        userRespDTO.setFirstName(user.getFirstName());
        userRespDTO.setLastName(user.getLastName());
        Set<String> roleNames = user.getRoles().stream().map(Role::name).collect(Collectors.toSet());
        userRespDTO.setRoles(roleNames);
        return userRespDTO;
    }

    @Override
    @Transactional
    public void logout() {
        User user = this.getCurrentUser();
        user.setRefreshToken(null);
        userRepository.save(user);
        SecurityContextHolder.clearContext();
    }

    @Override
    public List<UserRespDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::getResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserRespDTO createUser(CreateUserReqDTO createUserReqDTO) {
        User user = new User();
        user.setEmail(createUserReqDTO.getEmail());
        user.setFirstName(createUserReqDTO.getFirstName());
        user.setLastName(createUserReqDTO.getLastName());
        user.setPassword(passwordEncoder.encode(createUserReqDTO.getPassword()));
        user.setIsEmailVerificated(Boolean.FALSE);
        user.setRoles(Set.of(Role.USER));

        User saved = userRepository.save(user);
        return getResponseDTO(saved);
    }

    @Override
    public UserRespDTO updateUser(Long id, UpdateUserReqDTO updateUserReqDTO) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundError.USER_NOT_FOUND));
        existing.setFirstName(updateUserReqDTO.getFirstName());
        existing.setLastName(updateUserReqDTO.getLastName());
        if (updateUserReqDTO.getRoles() != null && !updateUserReqDTO.getRoles().isEmpty()) {
            existing.setRoles(updateUserReqDTO.getRoles());
        }
        User updated = userRepository.save(existing);
        return getResponseDTO(updated);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(NotFoundError.USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser")) {
            throw new AuthorizeException(AuthorizedError.NOT_CORRECT_TOKEN);
        }
        String email;
        if (authentication.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            email = authentication.getName();
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(NotFoundError.USER_NOT_FOUND));
    }

}
