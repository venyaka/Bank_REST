package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateUserReqDTO;
import com.example.bankcards.dto.request.UpdateUserReqDTO;
import com.example.bankcards.dto.request.UpdateCurrentUserReqDTO;
import com.example.bankcards.dto.response.UserRespDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AuthorizeException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtUtils;
import com.example.bankcards.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("hashed");
        user.setRoles(Set.of(Role.USER));
    }

    @Test
    void getCurrentUserInfo_success() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("user@example.com");
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        UserRespDTO dto = userService.getCurrentUserInfo();
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    void getCurrentUserInfo_notAuthenticated() {
        SecurityContextHolder.clearContext();
        assertThrows(AuthorizeException.class, () -> userService.getCurrentUserInfo());
    }

    @Test
    void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserRespDTO dto = userService.getUserById(1L);
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void getUserByEmail_success() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        UserRespDTO dto = userService.getUserByEmail("user@example.com");
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    void getUserByEmail_notFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserByEmail("notfound@example.com"));
    }

    @Test
    void createUser_success() {
        CreateUserReqDTO dto = new CreateUserReqDTO();
        dto.setEmail("new@example.com");
        dto.setFirstName("New");
        dto.setLastName("User");
        dto.setPassword("pass");
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        UserRespDTO resp = userService.createUser(dto);
        assertEquals("new@example.com", resp.getEmail());
    }

    @Test
    void updateUser_success() {
        UpdateUserReqDTO dto = new UpdateUserReqDTO();
        dto.setFirstName("Updated");
        dto.setLastName("User");
        dto.setRoles(Set.of(Role.ADMIN));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        UserRespDTO resp = userService.updateUser(1L, dto);
        assertEquals("Updated", resp.getFirstName());
        assertTrue(resp.getRoles().contains("ADMIN"));
    }

    @Test
    void updateUser_notFound() {
        UpdateUserReqDTO dto = new UpdateUserReqDTO();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.updateUser(99L, dto));
    }

    @Test
    void deleteUser_success() {
        doNothing().when(userRepository).deleteById(1L);
        assertDoesNotThrow(() -> userService.deleteUser(1L));
    }
}

