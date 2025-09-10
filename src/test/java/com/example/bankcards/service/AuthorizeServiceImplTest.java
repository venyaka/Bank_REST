package com.example.bankcards.service;

import com.example.bankcards.dto.request.RegisterReqDTO;
import com.example.bankcards.dto.request.UserAuthorizeReqDTO;
import com.example.bankcards.dto.response.TokenRespDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserSession;
import com.example.bankcards.exception.AuthorizeException;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtUtils;
import com.example.bankcards.service.impl.AuthorizeServiceImpl;
import com.example.bankcards.service.impl.CookieServiceImpl;
import com.example.bankcards.service.impl.MailServiceImpl;
import com.example.bankcards.service.impl.SessionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorizeServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MailServiceImpl mailService;
    @Mock
    private SessionServiceImpl sessionService;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private CookieServiceImpl cookieService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthorizeServiceImpl service;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("hashed");
        user.setIsEmailVerificated(true);
        user.setRoles(Set.of(Role.USER));
    }

    @Test
    void authorizeUser_success() {
        UserAuthorizeReqDTO dto = new UserAuthorizeReqDTO();
        dto.setEmail("user@gmail.com");
        dto.setPassword("12345");
        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("12345", "hashed")).thenReturn(true);
        when(jwtUtils.generateRandomSequence()).thenReturn("refresh");
        when(jwtUtils.generateToken(user)).thenReturn("jwt");
        when(jwtUtils.generateRefreshToken(user)).thenReturn("refreshJwt");
        when(userRepository.saveAndFlush(any())).thenReturn(user);
        doNothing().when(cookieService).addAuthCookies(any(), any(), any());
        when(sessionService.saveNewSession(user.getId())).thenReturn(new UserSession());
        ResponseEntity<TokenRespDTO> resp = service.authorizeUser(dto, response);
        assertEquals(200, resp.getStatusCodeValue());
        assertTrue(resp.getBody().getAccessToken().contains("Bearer jwt"));
    }

    @Test
    void authorizeUser_userNotFound() {
        UserAuthorizeReqDTO dto = new UserAuthorizeReqDTO();
        dto.setEmail("notfound@example.com");
        dto.setPassword("pass");
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertThrows(AuthorizeException.class, () -> service.authorizeUser(dto, response));
    }

    @Test
    void authorizeUser_wrongPassword() {
        UserAuthorizeReqDTO dto = new UserAuthorizeReqDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("wrong");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);
        assertThrows(AuthorizeException.class, () -> service.authorizeUser(dto, response));
    }

    @Test
    void authorizeUser_notVerified() {
        user.setIsEmailVerificated(false);
        UserAuthorizeReqDTO dto = new UserAuthorizeReqDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("pass");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "hashed")).thenReturn(true);
        assertThrows(AuthorizeException.class, () -> service.authorizeUser(dto, response));
    }

    @Test
    void registerUser_success() {
        RegisterReqDTO dto = new RegisterReqDTO();
        dto.setEmail("new@example.com");
        dto.setFirstName("New");
        dto.setLastName("User");
        dto.setPassword("pass");
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(mailService).sendUserVerificationMail(any(), any());
        assertDoesNotThrow(() -> service.registerUser(dto, request));
    }

    @Test
    void registerUser_emailExists() {
        RegisterReqDTO dto = new RegisterReqDTO();
        dto.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        assertThrows(BadRequestException.class, () -> service.registerUser(dto, request));
    }

    @Test
    void sendVerificationCode_success() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        user.setIsEmailVerificated(false);
        doNothing().when(mailService).sendUserVerificationMail(any(), any());
        assertDoesNotThrow(() -> service.sendVerificationCode("user@example.com", request));
    }

    @Test
    void sendVerificationCode_userNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.sendVerificationCode("notfound@example.com", request));
    }

    @Test
    void sendVerificationCode_alreadyVerified() {
        user.setIsEmailVerificated(true);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        assertThrows(BadRequestException.class, () -> service.sendVerificationCode("user@example.com", request));
    }

    @Test
    void verificateUser_success() {
        user.setIsEmailVerificated(false);
        user.setToken("token");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        assertDoesNotThrow(() -> service.verificateUser("user@example.com", "token"));
        assertTrue(user.getIsEmailVerificated());
        assertNull(user.getToken());
    }

    @Test
    void verificateUser_userNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.verificateUser("notfound@example.com", "token"));
    }

    @Test
    void verificateUser_alreadyVerified() {
        user.setIsEmailVerificated(true);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        assertThrows(BadRequestException.class, () -> service.verificateUser("user@example.com", "token"));
    }

    @Test
    void verificateUser_wrongToken() {
        user.setIsEmailVerificated(false);
        user.setToken("token");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        assertThrows(BadRequestException.class, () -> service.verificateUser("user@example.com", "wrong"));
    }
}

