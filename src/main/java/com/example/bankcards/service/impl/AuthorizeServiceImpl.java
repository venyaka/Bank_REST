package com.example.bankcards.service.impl;

import com.example.bankcards.exception.AuthorizeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.bankcards.dto.request.RegisterReqDTO;
import com.example.bankcards.dto.request.UserAuthorizeReqDTO;
import com.example.bankcards.dto.response.TokenRespDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.errors.AuthorizedError;
import com.example.bankcards.exception.errors.BadRequestError;
import com.example.bankcards.exception.errors.NotFoundError;
import com.example.bankcards.security.jwt.JwtUtils;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.AuthorizeService;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * Реализация сервиса для аутентификации и регистрации пользователей.
 */
@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class AuthorizeServiceImpl implements AuthorizeService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailServiceImpl mailService;
    private final SessionServiceImpl sessionService;
    private final JwtUtils jwtUtils;
    private final CookieServiceImpl cookieService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<TokenRespDTO> authorizeUser(UserAuthorizeReqDTO userAuthorizeDTO, HttpServletResponse response) {
        String userEmail = userAuthorizeDTO.getEmail();
        String userPassword = userAuthorizeDTO.getPassword();
        log.debug("Попытка авторизации пользователя: {}", userEmail);
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isEmpty()) {
            log.warn("Попытка авторизации с несуществующим email: {}", userEmail);
            throw new AuthorizeException(AuthorizedError.USER_WITH_THIS_EMAIL_NOT_FOUND);
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(userPassword, user.getPassword())) {
            log.warn("Неверный пароль для пользователя: {}", userEmail);
            throw new AuthorizeException(AuthorizedError.NOT_CORRECT_PASSWORD);
        }
        checkUserCanAuthorize(user);

        user.setRefreshToken(jwtUtils.generateRandomSequence());
        String jwtToken = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);
        userRepository.saveAndFlush(user);

        TokenRespDTO tokenDTO = new TokenRespDTO();
        tokenDTO.setAccessToken("Bearer " + jwtToken);
        tokenDTO.setRefreshToken("Bearer " + refreshToken);

        sessionService.saveNewSession(user.getId());
        cookieService.addAuthCookies(response, jwtToken, refreshToken);

        log.info("Пользователь {} успешно авторизован", userEmail);
        return ResponseEntity.ok(tokenDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void registerUser(@Valid RegisterReqDTO registerDTO, HttpServletRequest request) {
        log.debug("Попытка регистрации пользователя: {}", registerDTO.getEmail());
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            log.warn("Попытка регистрации с уже существующим email: {}", registerDTO.getEmail());
            throw new BadRequestException(BadRequestError.USER_ALREADY_EXISTS);
        }
        User user = new User();
        user.setEmail(registerDTO.getEmail());
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setIsEmailVerificated(Boolean.FALSE);
        user.setToken(generateValidatingToken());
        userRepository.save(user);

        mailService.sendUserVerificationMail(user, request);
        log.info("Пользователь {} успешно зарегистрирован, ожидается верификация email", registerDTO.getEmail());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendVerificationCode(String email, HttpServletRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(NotFoundError.USER_NOT_FOUND));
        if (user.getIsEmailVerificated()) {
            throw new BadRequestException(BadRequestError.USER_ALREADY_VERIFICATED);
        }
        mailService.sendUserVerificationMail(user, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void verificateUser(String email, String verificationToken) {
        log.debug("Попытка верификации пользователя: {}", email);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            log.warn("Верификация невозможна: пользователь {} не найден", email);
            throw new NotFoundException(NotFoundError.USER_NOT_FOUND);
        }
        User user = optionalUser.get();

        if (user.getIsEmailVerificated()) {
            log.warn("Пользователь {} уже верифицирован", email);
            throw new BadRequestException(BadRequestError.USER_ALREADY_VERIFICATED);
        }

        if (user.getToken() == null || !user.getToken().equals(verificationToken)) {
            log.warn("Неверный код верификации для пользователя: {}", email);
            throw new BadRequestException(BadRequestError.NOT_CORRECT_VERIFICATION_CODE);
        }

        user.setToken(null);
        user.setIsEmailVerificated(Boolean.TRUE);
        userRepository.save(user);
        log.info("Пользователь {} успешно верифицирован", email);
    }

    /**
     * Проверяет, может ли пользователь авторизоваться.
     *
     * @param user Пользователь для проверки.
     * @throws AuthorizeException если email пользователя не верифицирован.
     */
    private void checkUserCanAuthorize(User user) {
        if (!user.getIsEmailVerificated()) {
            throw new AuthorizeException(AuthorizedError.USER_NOT_VERIFY);
        }
    }

    /**
     * Генерирует случайный токен для валидации.
     *
     * @return Сгенерированный токен.
     */
    private String generateValidatingToken() {
        return RandomStringUtils.randomAlphanumeric(50);
    }
}
